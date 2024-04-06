package cn.winwang.winrpc.core.provider;

import cn.winwang.winrpc.core.api.RegistryCenter;
import cn.winwang.winrpc.core.consumer.ConsumerBootstrap;
import cn.winwang.winrpc.core.registry.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/16 17:20
 */
@Configuration
public class ProviderConfig {

    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBootstrap_runner(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> {
            System.out.println("providerBootstrap starting ...");
            providerBootstrap.start();
            System.out.println("providerBootstrap started ...");
        };
    }

    @Bean
    public RegistryCenter provide_rc() {
        return new ZkRegistryCenter();
    }
}
