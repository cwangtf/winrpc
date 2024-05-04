package cn.winwang.winrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class.
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
