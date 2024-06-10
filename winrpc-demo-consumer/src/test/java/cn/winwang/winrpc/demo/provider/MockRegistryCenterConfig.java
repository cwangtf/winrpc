package cn.winwang.winrpc.demo.provider;

import cn.winwang.winrpc.core.transport.MockWinRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/6/10 17:20
 */
@Configuration
public class MockRegistryCenterConfig {
    @Bean
    @ConditionalOnMissingBean
    MockWinRegistry mockWinRegistry() {
        return new MockWinRegistry();
    }
}
