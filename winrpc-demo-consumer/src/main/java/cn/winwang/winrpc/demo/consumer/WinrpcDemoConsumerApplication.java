package cn.winwang.winrpc.demo.consumer;

import cn.winwang.winrpc.core.annotation.WinConsumer;
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

import java.util.Arrays;

@SpringBootApplication
@Import({ConsumerConfig.class})
public class WinrpcDemoConsumerApplication {

    @WinConsumer
    UserService userService;

    @WinConsumer
    OrderService orderService;

    @Autowired
    Demo2 demo2;

    public static void main(String[] args) {
        SpringApplication.run(WinrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumer_runner() {
        return x -> {

            System.out.println(" userService.getId(10) = " + userService.getId(10));

            System.out.println(" userService.getId(10f) = " + userService.getId(10f));

            System.out.println(" userService.getId(new User(100, \"Win\")) = " + userService.getId(new User(100, "Win")));

            User user = userService.findById(1);
            System.out.println("RPC result userService.findById(1) = " + user);

            User user1 = userService.findById(1, "WinWang");
            System.out.println("RPC result userService.findById(1, \"WinWang\") = " + user1);

            System.out.println(userService.getName());

            System.out.println(userService.getName(123));

            System.out.println(userService.toString());

            System.out.println(userService.getId(11));

            System.out.println(userService.getName());

            System.out.println(" ===> userService.getIds()");
            for (int id : userService.getIds()) {
                System.out.println(id);
            }

            System.out.println(" ===> userService.getLongIds()");
            for (long id : userService.getLongIds()) {
                System.out.println(id);
            }

            System.out.println(" ===> userService.getLongIds()");
            for (int id : userService.getIds(new int[]{4,5,6})) {
                System.out.println(id);
            }

//            Order order = orderService.findById(2);
//            System.out.println("RPC result orderService.findById(2) = " + order);
//
//            demo2.test();

//            Order order404 = orderService.findById(404);
//            System.out.println("RPC result orderService.findById(404) = " + order404);
        };
    }

}
