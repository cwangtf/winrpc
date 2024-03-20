package cn.winwang.winrpc.core.consumer;

import cn.winwang.winrpc.core.api.RpcRequest;
import cn.winwang.winrpc.core.api.RpcResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/18 23:38
 */
public class WinInvocationHandler implements InvocationHandler {

    final static MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    Class<?> service;

    public WinInvocationHandler(Class<?> clazz) {
        this.service = clazz;
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
        rpcRequest.setMethod(method.getName());
        rpcRequest.setArgs(args);

        RpcResponse rpcResponse = post(rpcRequest);

        if (rpcResponse.isStatus()) {
            // TODO 处理基本类型
            Object data = rpcResponse.getData();
            if (data instanceof JSONObject) {
                JSONObject jsonResult = (JSONObject) data;
                return jsonResult.toJavaObject(method.getReturnType());
            } else {
                return data;
            }
        } else {
            Exception ex = rpcResponse.getEx();
            throw new RuntimeException(ex);
        }
    }

    OkHttpClient client = new OkHttpClient().newBuilder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS) // 连接建立后对侧发送数据读取超时
            .writeTimeout(1, TimeUnit.SECONDS) // 发给远程对侧超时
            .connectTimeout(1, TimeUnit.SECONDS) // 建立HTTP\TCP连接超时
            .build();

    private RpcResponse post(RpcRequest rpcRequest) {
        String reqJson = JSON.toJSONString(rpcRequest);
        System.out.println(" ===> reqJson = " + reqJson);
        Request request = new Request.Builder()
                .url("http://localhost:8080/")
                .post(RequestBody.create(reqJson, JSONTYPE))
                .build();
        try {
            String respJson = client.newCall(request).execute().body().string();
            System.out.println(" ===> respJson = " + respJson);
            RpcResponse rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
