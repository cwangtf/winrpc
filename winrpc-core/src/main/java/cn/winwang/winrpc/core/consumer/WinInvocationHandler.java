package cn.winwang.winrpc.core.consumer;

import cn.winwang.winrpc.core.api.RpcRequest;
import cn.winwang.winrpc.core.api.RpcResponse;
import cn.winwang.winrpc.core.util.MethodUtils;
import cn.winwang.winrpc.core.util.TypeUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
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
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        RpcResponse rpcResponse = post(rpcRequest);

        if (rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            Class<?> type = method.getReturnType();
            System.out.println("method.getReturnType() = " + type);
            if (data instanceof JSONObject jsonResult) {
                if (Map.class.isAssignableFrom(type)) {
                    Map resultMap = new HashMap();
                    Type genericReturnType = method.getGenericReturnType();
                    System.out.println(genericReturnType);
                    if (genericReturnType instanceof ParameterizedType parameterizedType) {
                        Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
                        System.out.println("keyType: " + keyType);
                        System.out.println("valueType: " + valueType);
                        jsonResult.entrySet().stream().forEach(
                                e -> {
                                    Object key = TypeUtils.cast(e.getKey(), keyType);
                                    Object value = TypeUtils.cast(e.getValue(), valueType);
                                    resultMap.put(key, value);
                                }
                        );
                    }
                    return resultMap;
                }
                return jsonResult.toJavaObject(type);
            } else if (data instanceof JSONArray jsonArray) {
                Object[] array = jsonArray.toArray();
                if (type.isArray()) {
                    Class<?> componentType = type.getComponentType();
                    Object resultArray = Array.newInstance(componentType, array.length);
                    for (int i = 0; i < array.length; i++) {
                        Array.set(resultArray, i, array[i]);
                    }
                    return resultArray;
                } else if (List.class.isAssignableFrom(type)) {
                    List<Object> resultList = new ArrayList<>(array.length);
                    Type genericReturnType = method.getGenericReturnType();
                    System.out.println(genericReturnType);
                    if (genericReturnType instanceof ParameterizedType parameterizedType) {
                        Type actualType = parameterizedType.getActualTypeArguments()[0];
                        System.out.println(actualType);
                        for (Object o : array) {
                            resultList.add(TypeUtils.cast(o, (Class<?>) actualType));
                        }
                    } else {
                        resultList.addAll(Arrays.asList(array));
                    }
                    return resultList;
                } else {
                    return null;
                }

            } else {
                return TypeUtils.cast(data,type);
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
