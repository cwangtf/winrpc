package cn.winwang.winrpc.core.config;

import cn.winwang.winrpc.core.api.*;
import cn.winwang.winrpc.core.cluster.GrayRouter;
import cn.winwang.winrpc.core.cluster.RoundRobinLoadBalancer;
import cn.winwang.winrpc.core.consumer.ConsumerBootstrap;
import cn.winwang.winrpc.core.filter.ContextParameterFilter;
import cn.winwang.winrpc.core.meta.InstanceMeta;
import cn.winwang.winrpc.core.registry.win.WinRegisterCenter;
import cn.winwang.winrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * Config for consumer.
 *
 * @author winwang
 * @date 2024/3/18 23:22
 */
@Slf4j
@Configuration
@Import({AppProperties.class, ConsumerProperties.class})
public class ConsumerConfig {

    @Autowired
    AppProperties appProperties;

    @Autowired
    ConsumerProperties consumerProperties;

//    @Bean
//    @ConditionalOnMissingBean
//    @ConditionalOnProperty(prefix = "apollo.bootstrap", value = "enabled")
//    ApolloChangedListener consumer_apolloChangedListener() {
//        return new ApolloChangedListener();
//    }

    @Bean
    ConsumerBootstrap createConsumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE + 1)
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumer) {
        return x -> {
            log.info("consumerBootstrap starting ...");
            consumer.start();
            log.info("consumerBootstrap started ...");
        };
    }

    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer() {
        return new RoundRobinLoadBalancer<>();
    }

    @Bean
    public Router<InstanceMeta> router() {
        return new GrayRouter(consumerProperties.getGrayRatio());
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public RegistryCenter consumer_rc() {
        return new WinRegisterCenter();
//        return new ZkRegistryCenter();
    }

    @Bean
    public Filter defaultFilter() {
        return new ContextParameterFilter();
    }

    @Bean
    @RefreshScope // context.refresh
    public RpcContext createContext(@Autowired Router router,
                                    @Autowired LoadBalancer loadBalancer,
                                    @Autowired List<Filter> filters) {
        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);
        context.setFilters(filters);
        context.getParameters().put("app.id", appProperties.getId());
        context.getParameters().put("app.namespace", appProperties.getNamespace());
        context.getParameters().put("app.env", appProperties.getEnv());
        context.setConsumerProperties(consumerProperties);
        return context;
    }

}
