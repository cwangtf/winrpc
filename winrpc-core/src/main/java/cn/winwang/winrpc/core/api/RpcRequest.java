package cn.winwang.winrpc.core.api;

import lombok.Data;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/16 15:16
 */
@Data
public class RpcRequest {

    private String service; // 接口:cn.winwang.winrpc.demo.api.UserService
    private String method; // 方法: findById
    private Object[] args; // 参数: 100

}

