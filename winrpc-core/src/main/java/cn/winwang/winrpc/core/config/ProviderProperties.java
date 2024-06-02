package cn.winwang.winrpc.core.config;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import lombok.Data;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/5/26 19:00
 */
@Data
@ConfigurationProperties(prefix = "winrpc.provider")
public class ProviderProperties {

    // for provider

    Map<String, String> metas = new HashMap<>();

    String test;

    public void setTest(String test) {
        this.test = test;
    }

}
