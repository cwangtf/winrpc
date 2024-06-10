package cn.winwang.winrpc.core.config;

import cn.winwang.winrpc.core.api.RegistryCenter;
import cn.winwang.winrpc.core.provider.ProviderBootstrap;
import cn.winwang.winrpc.core.provider.ProviderInvoker;
import cn.winwang.winrpc.core.registry.win.WinRegisterCenter;
import cn.winwang.winrpc.core.registry.zk.ZkRegistryCenter;
import cn.winwang.winrpc.core.transport.SpringBootTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

/**
 * provider config class.
 *
 * @author winwang
 * @date 2024/3/16 17:20
 */
@Slf4j
@Configuration
@Import({ProviderProperties.class, AppProperties.class, SpringBootTransport.class})
public class ProviderConfig {

    @Value("${server.port:8080}")
    private String port;

//    @Bean
//    @ConditionalOnMissingBean
//    @ConditionalOnProperty(prefix = "apollo.bootstrap", value = "enabled")
//    ApolloChangedListener provider_apolloChangedListener() {
//        return new ApolloChangedListener();
//    }

    @Bean
    ProviderBootstrap providerBootstrap(@Autowired AppProperties ap,
                                        @Autowired ProviderProperties pp) {
        return new ProviderBootstrap(port, ap, pp);
    }

    @Bean
    ProviderInvoker providerInvoker(@Autowired ProviderBootstrap provider) {
        return new ProviderInvoker(provider);
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBootstrap_runner(@Autowired ProviderBootstrap provider) {
        return x -> {
            log.info("providerBootstrap starting ...");
            provider.start();
            log.info("providerBootstrap started ...");
        };
    }

    @Bean
    public RegistryCenter provide_rc() {
        return new WinRegisterCenter();
//        return new ZkRegistryCenter();
    }
}
