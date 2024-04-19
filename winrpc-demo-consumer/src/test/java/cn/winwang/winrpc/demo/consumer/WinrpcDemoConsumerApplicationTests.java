package cn.winwang.winrpc.demo.consumer;

import cn.winwang.winrpc.demo.provider.WinrpcDemoProviderApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class WinrpcDemoConsumerApplicationTests {

    static ApplicationContext context;
    @BeforeAll
    static void init() {
        context = SpringApplication.run(WinrpcDemoProviderApplication.class, "--server.port=8084", "--logging.level.cn.winwang.winrpc=debug");
    }

    @Test
    void contextLoads() {
        System.out.println(" ===> aaaa .... ");
    }

    @AfterAll
    static void destroy() {
        SpringApplication.exit(context, () -> 1);
    }

}
