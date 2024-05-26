package cn.winwang.winrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/5/26 19:00
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "winrpc.provider")
public class ProviderConfigProperties {

    // for provider

    Map<String, String> metas;

}
