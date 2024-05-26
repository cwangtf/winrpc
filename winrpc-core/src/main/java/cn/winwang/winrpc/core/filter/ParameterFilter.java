package cn.winwang.winrpc.core.filter;

import cn.winwang.winrpc.core.api.Filter;
import cn.winwang.winrpc.core.api.RpcContext;
import cn.winwang.winrpc.core.api.RpcRequest;
import cn.winwang.winrpc.core.api.RpcResponse;

import java.util.Map;

/**
 * 处理上下文参数.
 *
 * @author winwang
 * @date 2024/5/26 16:21
 */
public class ParameterFilter implements Filter {
    @Override
    public Object prefilter(RpcRequest request) {
        Map<String, String> params = RpcContext.ContextParameters.get();
        if(!params.isEmpty()) {
            request.getParams().putAll(params);
        }
        return null;
    }

    @Override
    public Object postfilter(RpcRequest request, RpcResponse response, Object result) {
        // RpcContext.ContextParameters.get().clear();
        return null;
    }
}
