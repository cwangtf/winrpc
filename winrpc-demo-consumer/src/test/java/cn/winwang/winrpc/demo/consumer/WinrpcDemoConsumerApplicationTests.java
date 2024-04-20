package cn.winwang.winrpc.demo.consumer;

import cn.winwang.winrpc.core.test.TestZKServer;
import cn.winwang.winrpc.demo.provider.WinrpcDemoProviderApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = {WinrpcDemoConsumerApplication.class})
class WinrpcDemoConsumerApplicationTests {

    static ApplicationContext context;

    static TestZKServer zkServer = new TestZKServer();

    @BeforeAll
    static void init() {
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        zkServer.start();
        context = SpringApplication.run(WinrpcDemoProviderApplication.class, "--server.port=8094",
                "--winrpc.zkServer=localhost:2182", "--logging.level.cn.winwang.winrpc=info");
    }

    @Test
    void contextLoads() {
        System.out.println(" ===> aaaa .... ");
    }

    @AfterAll
    static void destroy() {
        SpringApplication.exit(context, () -> 1);
        zkServer.stop();
    }

}
