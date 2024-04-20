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

    static ApplicationContext context1;
    static ApplicationContext context2;

    static TestZKServer zkServer = new TestZKServer();

    @BeforeAll
    static void init() {
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============     ZK2182    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        zkServer.start();
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8094    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        context1 = SpringApplication.run(WinrpcDemoProviderApplication.class,
                "--server.port=8094", "--winrpc.zkServer=localhost:2182",
                "--logging.level.cn.winwang.winrpc=info");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8095    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        context2 = SpringApplication.run(WinrpcDemoProviderApplication.class,
                "--server.port=8095", "--winrpc.zkServer=localhost:2182",
                "--logging.level.cn.winwang.winrpc=info");
    }

    @Test
    void contextLoads() {
        System.out.println(" ===> aaaa .... ");
    }

    @AfterAll
    static void destroy() {
        SpringApplication.exit(context1, () -> 1);
        SpringApplication.exit(context2, () -> 1);
        zkServer.stop();
    }

}
