package cn.winwang.winrpc.demo.provider;

import cn.winwang.winconfig.client.annotation.EnableWinConfig;
import cn.winwang.winrpc.core.api.RpcRequest;
import cn.winwang.winrpc.core.api.RpcResponse;
//import cn.winwang.winrpc.core.config.ApolloChangedListener;
import cn.winwang.winrpc.core.config.ProviderConfig;
import cn.winwang.winrpc.core.config.ProviderProperties;
import cn.winwang.winrpc.core.transport.SpringBootTransport;
import cn.winwang.winrpc.demo.api.User;
import cn.winwang.winrpc.demo.api.UserService;
//import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Import({ProviderConfig.class})
@SpringBootApplication
//@EnableApolloConfig
@EnableWinConfig
public class WinrpcDemoProviderApplication {

//    @Bean
//    ApolloChangedListener apolloChangedListener() {
//        return new ApolloChangedListener();
//    }

    public static void main(String[] args) {
        SpringApplication.run(WinrpcDemoProviderApplication.class, args);
    }

    // 使用HTTP + JSON 来实现序列化和通信

//    @Value("${winrpc.provider.test}")
//    String test;
//
    @Autowired
ProviderProperties providerProperties;

    @RequestMapping("/metas")
    public String meta() {
        System.out.println(System.identityHashCode(providerProperties.getMetas()));
        return providerProperties.getMetas().toString();
    }

    @Autowired
    UserService userService;

    @RequestMapping("/ports")
    public RpcResponse ports(@RequestParam("ports") String ports) {
        userService.setTimeoutPorts(ports);
        RpcResponse<String> response = new RpcResponse<>();
        response.setStatus(true);
        response.setData("OK:" + ports);
        return response;
    }

    @Bean
    ApplicationRunner providerRun(@Autowired ApplicationContext context) {
        return x -> {

            System.out.println(" =====> providerProperties.getMetas()");
            providerProperties.getMetas().forEach((k,v)->System.out.println(k+":"+v));

            ConfigurationPropertiesRebinder rebinder = context.getBean(ConfigurationPropertiesRebinder.class);
            System.out.println(rebinder);
            testAll();
        };
    }

    @Autowired
    SpringBootTransport transport;

    private void testAll() {
        // test 1 parameters method
        System.out.println("Provider Case 1. >>===[基本测试：1个参数]===");
        RpcRequest request = new RpcRequest();
        request.setService("cn.winwang.winrpc.demo.api.UserService");
        request.setMethodSign("findById@1_int");
        request.setArgs(new Object[]{100});

        RpcResponse<Object> rpcResponse = transport.invoke(request);
        System.out.println("return : " + rpcResponse.getData());

        // test 2 parameters method
        System.out.println("Provider Case 2. >>===[基本测试：2个参数]===");
        RpcRequest request1 = new RpcRequest();
        request1.setService("cn.winwang.winrpc.demo.api.UserService");
        request1.setMethodSign("findById@2_int_java.lang.String");
        request1.setArgs(new Object[]{100, "CC"});

        RpcResponse<Object> rpcResponse1 = transport.invoke(request1);
        System.out.println("return : " + rpcResponse1.getData());

        // test 3 for List<User> method&parameter
        System.out.println("Provider Case 3. >>===[复杂测试：参数类型为List<User>]===");
        RpcRequest request3 = new RpcRequest();
        request3.setService("cn.winwang.winrpc.demo.api.UserService");
        request3.setMethodSign("getList@1_java.util.List");
        List<User> userList = new ArrayList<>();
        userList.add(new User(100, "WinWang100"));
        userList.add(new User(101, "WinWang101"));
        request3.setArgs(new Object[]{ userList });
        RpcResponse<Object> rpcResponse3 = transport.invoke(request3);
        System.out.println("return : "+rpcResponse3.getData());

        // test 4 for Map<String, User> method&parameter
        System.out.println("Provider Case 4. >>===[复杂测试：参数类型为Map<String, User>]===");
        RpcRequest request4 = new RpcRequest();
        request4.setService("cn.winwang.winrpc.demo.api.UserService");
        request4.setMethodSign("getMap@1_java.util.Map");
        Map<String, User> userMap = new HashMap<>();
        userMap.put("P100", new User(100, "WinWang100"));
        userMap.put("P101", new User(101, "WinWang101"));
        request4.setArgs(new Object[]{ userMap });
        RpcResponse<Object> rpcResponse4 = transport.invoke(request4);
        System.out.println("return : "+rpcResponse4.getData());

        // test 5 for traffic control
        System.out.println("Provider Case 5. >>===[复杂测试：测试流量并发控制]===");
//        for (int i = 0; i < 120; i++) {
//            try {
//                Thread.sleep(1000);
//                RpcResponse<Object> r = transport.invoke(request);
//                System.out.println(i + " ***>>> " +r.getData());
//            } catch (RpcException e) {
//                // ignore
//                System.out.println(i + " ***>>> " +e.getMessage() + " -> " + e.getErrcode());
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }

    }


}
