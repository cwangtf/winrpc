package cn.winwang.winrpc.core.filter;

import cn.winwang.winrpc.core.api.Filter;
import cn.winwang.winrpc.core.api.RpcRequest;
import cn.winwang.winrpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/4/18 22:18
 */
public class CacheFilter implements Filter {

    // 替换成guava cache，加容量和过期时间 todo
    static Map<String, Object> cache = new ConcurrentHashMap();

    @Override
    public Object prefilter(RpcRequest request) {
        return cache.get(request.toString());
    }

    @Override
    public Object postfilter(RpcRequest request, RpcResponse response, Object result) {
        cache.putIfAbsent(request.toString(), result);
        return result;
    }
}
