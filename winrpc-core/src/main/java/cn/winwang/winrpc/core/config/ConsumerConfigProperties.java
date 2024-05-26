package cn.winwang.winrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/5/26 18:59
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "winrpc.consumer")
public class ConsumerConfigProperties {

    // for ha and governance
    private int retries;

    private int timeout;

    private int faultLimit;

    private int halfOpenInitialDelay;

    private int halfOpenDelay;

    private int grayRatio;

}
