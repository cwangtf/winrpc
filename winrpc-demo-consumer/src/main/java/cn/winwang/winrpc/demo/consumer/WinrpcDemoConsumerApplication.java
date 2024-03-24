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
            User user = userService.findById(1);
            System.out.println("RPC result userService.findById(1) = " + user);

            User user1 = userService.findById(1, "WinWang");
            System.out.println("RPC result userService.findById(1, \"WinWang\") = " + user1);

            System.out.println(userService.getName());

            System.out.println(userService.getName(123));

//            System.out.println(userService.toString());
//
//            System.out.println(userService.getId(11));
//
//            System.out.println(userService.getName());

//            Order order = orderService.findById(2);
//            System.out.println("RPC result orderService.findById(2) = " + order);
//
//            demo2.test();

//            Order order404 = orderService.findById(404);
//            System.out.println("RPC result orderService.findById(404) = " + order404);
        };
    }

}
