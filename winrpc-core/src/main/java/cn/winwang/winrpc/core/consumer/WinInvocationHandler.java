package cn.winwang.winrpc.core.consumer;

import cn.winwang.winrpc.core.api.RpcContext;
import cn.winwang.winrpc.core.api.RpcRequest;
import cn.winwang.winrpc.core.api.RpcResponse;
import cn.winwang.winrpc.core.consumer.http.OkHttpInvoker;
import cn.winwang.winrpc.core.util.MethodUtils;

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
public class WinInvocationHandler implements InvocationHandler {
    Class<?> service;

    RpcContext context;

    List<String> providers;

    HttpInvoker httpInvoker = new OkHttpInvoker();

    public WinInvocationHandler(Class<?> clazz, RpcContext context, List<String> providers) {
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

        List<String> urls = context.getRouter().route(this.providers);
        String url = (String) context.getLoadBalancer().choose(urls);
        System.out.println("loadBalancer.choose(urls) ==> " + url);
        RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, url);

        if (rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            return castMethodResult(method, data);
        } else {
            Exception ex = rpcResponse.getEx();
            throw new RuntimeException(ex);
        }
    }

}
