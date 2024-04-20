package cn.winwang.winrpc.core.consumer.http;

import cn.winwang.winrpc.core.api.RpcRequest;
import cn.winwang.winrpc.core.api.RpcResponse;
import cn.winwang.winrpc.core.consumer.HttpInvoker;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/4/10 23:42
 */
@Slf4j
public class OkHttpInvoker implements HttpInvoker {

    final static MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client;

    public OkHttpInvoker(int timeout) {
        client = new OkHttpClient().newBuilder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(timeout, TimeUnit.MICROSECONDS) // 连接建立后对侧发送数据读取超时
                .writeTimeout(timeout, TimeUnit.MICROSECONDS) // 发给远程对侧超时
                .connectTimeout(timeout, TimeUnit.MICROSECONDS) // 建立HTTP\TCP连接超时
                .build();
    }

    @Override
    public RpcResponse<?> post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        log.debug(" ===> reqJson = " + reqJson);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson, JSONTYPE))
                .build();
        try {
            String respJson = client.newCall(request).execute().body().string();
            log.debug(" ===> respJson = " + respJson);
            RpcResponse<Object> rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            return rpcResponse;
        } catch (Exception e) {
            // e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
