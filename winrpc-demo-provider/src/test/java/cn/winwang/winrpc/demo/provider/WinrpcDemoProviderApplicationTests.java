package cn.winwang.winrpc.demo.provider;

import cn.winwang.winrpc.core.test.TestZKServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WinrpcDemoProviderApplicationTests {

    static TestZKServer zkServer = new TestZKServer();

    @BeforeAll
    static void init() {
        zkServer.start();
    }

    @Test
    void contextLoads() {
        System.out.println(" ===> WinrpcDemoProviderApplicationTests  .... ");
    }

    @AfterAll
    static void destory() {
        zkServer.stop();
    }

}
