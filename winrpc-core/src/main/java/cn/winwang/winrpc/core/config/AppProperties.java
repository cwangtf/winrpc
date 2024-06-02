package cn.winwang.winrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * config app properties.
 *
 * @author winwang
 * @date 2024/5/26 18:57
 */
@Data
@ConfigurationProperties(prefix = "winrpc.app")
public class AppProperties {

    // for app instance
    private String id = "app1";;

    private String namespace = "public";

    private String env = "dev";

}
