package cn.winwang.winrpc.core.consumer;

import cn.winwang.winrpc.core.api.Filter;
import cn.winwang.winrpc.core.api.LoadBalancer;
import cn.winwang.winrpc.core.api.RegistryCenter;
import cn.winwang.winrpc.core.api.Router;
import cn.winwang.winrpc.core.cluster.RoundRobinLoadBalancer;
import cn.winwang.winrpc.core.filter.CacheFilter;
import cn.winwang.winrpc.core.filter.MockFilter;
import cn.winwang.winrpc.core.meta.InstanceMeta;
import cn.winwang.winrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/18 23:22
 */
@Slf4j
@Configuration
public class ConsumerConfig {

    @Value("${winrpc.providers}")
    String servers;

    @Bean
    ConsumerBootstrap createConsumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            log.info("consumerBootstrap starting ...");
            consumerBootstrap.start();
            log.info("consumerBootstrap started ...");
        };
    }

    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer() {
//        return LoadBalancer.Default;
//        return new RandomLoadBalancer();
        return new RoundRobinLoadBalancer<>();
    }

    @Bean
    public Router<InstanceMeta> router() {
        return Router.Default;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumer_rc() {
        return new ZkRegistryCenter();
    }

    @Bean
    public Filter defaultFilter() {
        return Filter.Default;
    }

    @Bean
    public Filter filter2() {
        return new CacheFilter();
    }

//    @Bean
//    public Filter filter2() {
//        return new MockFilter();
//    }
}
