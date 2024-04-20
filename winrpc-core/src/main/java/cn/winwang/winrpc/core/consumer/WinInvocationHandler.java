package cn.winwang.winrpc.core.consumer;

import cn.winwang.winrpc.core.api.*;
import cn.winwang.winrpc.core.consumer.http.OkHttpInvoker;
import cn.winwang.winrpc.core.meta.InstanceMeta;
import cn.winwang.winrpc.core.util.MethodUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.List;

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

    List<InstanceMeta> providers;

    HttpInvoker httpInvoker;

    public WinInvocationHandler(Class<?> clazz, RpcContext context, List<InstanceMeta> providers) {
        this.service = clazz;
        this.context = context;
        this.providers = providers;
        int timeout = Integer.parseInt(context.getParameters().getOrDefault("", "1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
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

        int retries = Integer.parseInt(context.getParameters().getOrDefault("app.retries", "1"));
        while (retries -- > 0) {
            log.debug(" ===> retries: " + retries);
            try {
                for (Filter filter : this.context.getFilterList()) {
                    Object preResult = filter.prefilter(rpcRequest);
                    if (preResult != null) {
                        log.debug(filter.getClass().getName() + " ===> prefilter: " + preResult);
                        return preResult;
                    }
                }

                List<InstanceMeta> instances = context.getRouter().route(this.providers);
                InstanceMeta instance = context.getLoadBalancer().choose(instances);
                log.debug("loadBalancer.choose(instances) ==> " + instance);

                RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());
                Object result = castReturnResult(method, rpcResponse);

                for (Filter filter : this.context.getFilterList()) {
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

    private static Object castReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.isStatus()) {
            return castMethodResult(method, rpcResponse.getData());
        } else {
            Exception exception = rpcResponse.getEx();
            if (exception instanceof  RpcException ex) {
                throw ex;
            } else {
                throw new RpcException(rpcResponse.getEx(), RpcException.UnknownEx);
            }
        }
    }

}
