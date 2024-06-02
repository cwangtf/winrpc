package cn.winwang.winrpc.core.consumer;

import cn.winwang.winrpc.core.api.*;
import cn.winwang.winrpc.core.consumer.http.OkHttpInvoker;
import cn.winwang.winrpc.core.governance.SlidingTimeWindow;
import cn.winwang.winrpc.core.meta.InstanceMeta;
import cn.winwang.winrpc.core.util.MethodUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cn.winwang.winrpc.core.util.TypeUtils.castMethodResult;

/**
 * 消费端的动态代理处理类.
 *
 * @author winwang
 * @date 2024/3/18 23:38
 */
@Slf4j
public class WinInvocationHandler implements InvocationHandler {
    Class<?> service;

    RpcContext context;

    final List<InstanceMeta> providers;

    final List<InstanceMeta> isolatedProviders = new ArrayList<>();

    final List<InstanceMeta> halfOpenProviders = new ArrayList<>();

    final Map<String, SlidingTimeWindow> windows = new HashMap<>();

    HttpInvoker httpInvoker;

    ScheduledExecutorService executor;

    public WinInvocationHandler(Class<?> clazz, RpcContext context, List<InstanceMeta> providers) {
        this.service = clazz;
        this.context = context;
        this.providers = providers;
        int timeout = context.getConsumerProperties().getTimeout();
        this.httpInvoker = new OkHttpInvoker(timeout);
        this.executor = Executors.newScheduledThreadPool(1);
        int halfOpenInitialDelay = context.getConsumerProperties().getHalfOpenInitialDelay();
        int halfOpenDelay = context.getConsumerProperties().getHalfOpenDelay();
        this.executor.scheduleWithFixedDelay(this::halfOpen, halfOpenInitialDelay,
                halfOpenDelay, TimeUnit.MILLISECONDS);
    }

    private void halfOpen() {
        log.debug("  ====> half open isolatedProviders: " + isolatedProviders);
        halfOpenProviders.clear();
        halfOpenProviders.addAll(isolatedProviders);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String name = method.getName();
        if (name.equals("toString") || name.equals("hashCode")) {
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        // class全限定名称
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        int retries = context.getConsumerProperties().getRetries();
        int faultLimit = context.getConsumerProperties().getFaultLimit();
        while (retries -- > 0) {
            log.debug(" ===> retries: " + retries);
            try {
                for (Filter filter : this.context.getFilters()) {
                    Object preResult = filter.prefilter(rpcRequest);
                    if (preResult != null) {
                        log.debug(filter.getClass().getName() + " ===> prefilter: " + preResult);
                        return preResult;
                    }
                }

                InstanceMeta instance;
                synchronized (halfOpenProviders) {
                    if (halfOpenProviders.isEmpty()) {
                        List<InstanceMeta> instances = context.getRouter().route(this.providers);
                        instance = context.getLoadBalancer().choose(instances);
                        log.debug(" loadBalancer.choose(instances) ==> {}", instance);
                    } else {
                        instance = halfOpenProviders.remove(0);
                        log.debug(" check alive instance ===> {}", instance);
                    }
                }

                RpcResponse<?> rpcResponse;
                Object result;

                String url = instance.toUrl();
                try {
                    rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());
                    result = castReturnResult(method, rpcResponse);
                } catch (Exception e) {
                    // 故障的规则统计和隔离
                    // 每一次异常，记录一次，统计30s的异常数。

                    synchronized (windows) {
                        SlidingTimeWindow window = windows.computeIfAbsent(url, k -> new SlidingTimeWindow());

                        window.record(System.currentTimeMillis());
                        log.debug("instance {} in window with {}", url, window.getSum());
                        // 发生了10次，就做故障隔离
                        if (window.getSum() >= faultLimit) {
                            isolate(instance);
                        }
                    }

                    throw e;

                }

                synchronized (providers) {
                    if (!providers.contains(instance)) {
                        isolatedProviders.remove(instance);
                        providers.add(instance);
                        log.debug("instance {} is recovered, isolatedProviders={}, providers={}"
                                , instance, isolatedProviders, providers);
                    }
                }

                for (Filter filter : this.context.getFilters()) {
                    Object filterResult = filter.postfilter(rpcRequest, rpcResponse, result);
                    if (filterResult != null) {
                        return filterResult;
                    }
                }
                return result;
            } catch (Exception ex) {
                if (!(ex.getCause() instanceof SocketTimeoutException)) {
                    throw ex;
                }
            }
        }

        return null;
    }

    private void isolate(InstanceMeta instance) {
        log.debug(" ==> isolate instance: " + instance);
        providers.remove(instance);
        log.debug(" ==> providers = {}", providers);
        isolatedProviders.add(instance);
        log.debug(" ==> isolatedProviders = {}", isolatedProviders);
    }

    private static Object castReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.isStatus()) {
            return castMethodResult(method, rpcResponse.getData());
        } else {
            RpcException exception = rpcResponse.getEx();
            if (exception != null) {
                log.error("response error.", exception);
                throw exception;
            }
            return null;
        }
    }

}
