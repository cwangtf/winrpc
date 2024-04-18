package cn.winwang.winrpc.core.consumer;

import cn.winwang.winrpc.core.api.Filter;
import cn.winwang.winrpc.core.api.RpcContext;
import cn.winwang.winrpc.core.api.RpcRequest;
import cn.winwang.winrpc.core.api.RpcResponse;
import cn.winwang.winrpc.core.consumer.http.OkHttpInvoker;
import cn.winwang.winrpc.core.meta.InstanceMeta;
import cn.winwang.winrpc.core.util.MethodUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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

    HttpInvoker httpInvoker = new OkHttpInvoker();

    public WinInvocationHandler(Class<?> clazz, RpcContext context, List<InstanceMeta> providers) {
        this.service = clazz;
        this.context = context;
        this.providers = providers;
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

        return castReturnResult(method, rpcResponse);
    }

    private static Object castReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            return castMethodResult(method, data);
        } else {
            Exception ex = rpcResponse.getEx();
            throw new RuntimeException(ex);
        }
    }

}
