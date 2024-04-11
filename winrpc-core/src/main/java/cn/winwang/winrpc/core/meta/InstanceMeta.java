package cn.winwang.winrpc.core.meta;

import lombok.Data;

import java.util.Map;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/4/11 23:36
 */
@Data
public class InstanceMeta {

    private String scheme;

    private String host;

    private Integer port;

    private String context;

    private boolean status; // online or offline

    private Map<String, String> parameters;

    public InstanceMeta(String scheme, String host, Integer port, String context) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public String toPath() {
        return String.format("%s_%d", host, port);
    }

    public static InstanceMeta http(String host, Integer port) {
        return new InstanceMeta("http", host, port, "");
    }

    @Override
    public String toString() {
        return String.format("%s://%s:%d/%s", scheme, host, port, context);
    }
}
