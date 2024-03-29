package cn.winwang.winrpc.demo.consumer;

import cn.winwang.winrpc.core.annotation.WinConsumer;
import cn.winwang.winrpc.core.api.RpcRequest;
import cn.winwang.winrpc.core.api.RpcResponse;
import cn.winwang.winrpc.core.consumer.ConsumerConfig;
import cn.winwang.winrpc.demo.api.Order;
import cn.winwang.winrpc.demo.api.OrderService;
import cn.winwang.winrpc.demo.api.User;
import cn.winwang.winrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
@Import({ConsumerConfig.class})
public class WinrpcDemoConsumerApplication {

    @WinConsumer
    UserService userService;

//    @WinConsumer
//    OrderService orderService;
//
//    @Autowired
//    Demo2 demo2;

    @RequestMapping("/")
    public User findBy(int id) {
        return userService.findById(id);
    }

    public static void main(String[] args) {
        SpringApplication.run(WinrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumer_runner() {
        return x -> {

            // 常规int类型，返回User对象
            User user = userService.findById(1);
            System.out.println("RPC result userService.findById(1) = " + user);

            // 测试方法重载，同名方法，参数不同
            User user1 = userService.findById(1, "WinWang");
            System.out.println("RPC result userService.findById(1, \"WinWang\") = " + user1);

            // 测试返回字符串
            System.out.println(userService.getName());

            // 测试重载方法返回字符串
            System.out.println(userService.getName(123));

            // 测试local toString方法
            System.out.println(userService.toString());

            // 测试long类型
            System.out.println(" userService.getId(10) = " + userService.getId(10));

            // 测试long+float类型
            System.out.println(" userService.getId(10f) = " + userService.getId(10f));

            // 测试参数是User类型
            System.out.println(" userService.getId(new User(100,\"WinWang\")) = " +
                    userService.getId(new User(100,"WinWang")));


            System.out.println(" 测试返回long[] ===> userService.getLongIds()");
            for (int id : userService.getIds()) {
                System.out.println(id);
            }

            System.out.println(" ===> userService.getLongIds()");
            for (long id : userService.getLongIds()) {
                System.out.println(id);
            }

            System.out.println(" 测试参数和返回值都是long[] ===> userService.getLongIds()");
            for (int id : userService.getIds(new int[]{4,5,6})) {
                System.out.println(id);
            }

            // 测试参数和返回值都是List类型
            List<User> list = userService.getList(List.of(
                    new User(100, "Win100"),
                    new User(101, "Win101")));
            list.forEach(System.out::println);

            // 测试参数和返回值都是Map类型
            Map<String, User> map = new HashMap<>();
            map.put("A200", new User(200, "Win200"));
            map.put("A201", new User(201, "Win201"));
            userService.getMap(map).forEach(
                    (k,v) -> System.out.println(k + " -> " + v)
            );
        };
    }

}
