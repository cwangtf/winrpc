package cn.winwang.winrpc.core.api;

import lombok.Data;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/4/20 16:47
 */
@Data
public class WinrpcException extends RuntimeException {

    private String errcode;

    public WinrpcException() {
    }

    public WinrpcException(String message) {
        super(message);
    }

    public WinrpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public WinrpcException(Throwable cause) {
        super(cause);
    }

    public WinrpcException(Throwable cause, String errcode) {
        super(cause);
        this.errcode = errcode;
    }

    // X => 技术类异常:
    // Y => 业务类异常:
    // Z => unknown, 搞不清楚, 再归类到X或Y
    public static final String SocketTimeoutEx = "X001" + "-" + "http_invoke_timeout";
    public static final String NoSuchMethodEx = "X002" + "-" + "method_not_exists";
    public static final String UnknownEx = "Z001" + "-" + "unknown";

}
