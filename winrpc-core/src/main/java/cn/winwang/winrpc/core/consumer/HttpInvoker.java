package cn.winwang.winrpc.core.consumer;

import cn.winwang.winrpc.core.api.RpcRequest;
import cn.winwang.winrpc.core.api.RpcResponse;

/**
 * Interface for http invoke.
 *
 * @author winwang
 * @date 2024/4/10 22:06
 */
public interface HttpInvoker {

    RpcResponse<?> post(RpcRequest rpcRequest, String url);

}
