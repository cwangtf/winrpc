package cn.winwang.winrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * response data for RPC call.
 *
 * @author winwang
 * @date 2024/3/16 15:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> {

    boolean status;  // 状态:true

    T data; // new User

    RpcException ex;
}
