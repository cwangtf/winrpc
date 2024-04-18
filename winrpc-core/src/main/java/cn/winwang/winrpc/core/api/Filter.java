package cn.winwang.winrpc.core.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 过滤器.
 *
 * @author winwang
 * @date 2024/3/26 23:37
 */
public interface Filter {

    static Map<?, ?> cache = new ConcurrentHashMap();

    Object prefilter(RpcRequest request);

    Object postfilter(RpcRequest request, RpcResponse response, Object result);

    // Filter next();

    Filter Default = new Filter() {
        @Override
        public RpcResponse prefilter(RpcRequest request) {
            return null;
        }

        @Override
        public Object postfilter(RpcRequest request, RpcResponse response, Object result) {
            return null;
        }
    };

}
