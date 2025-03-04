package cn.winwang.winrpc.demo.consumer;

import cn.winwang.winrpc.core.annotation.WinConsumer;
import cn.winwang.winrpc.core.api.Router;
import cn.winwang.winrpc.core.api.RpcContext;
import cn.winwang.winrpc.core.cluster.GrayRouter;
import cn.winwang.winrpc.core.config.ConsumerConfig;
import cn.winwang.winrpc.demo.api.User;
import cn.winwang.winrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping("/api/")
    public User findBy(@RequestParam("id") int id) {
        return userService.findById(id);
    }

    @RequestMapping("/find/")
    public User find(@RequestParam("timeout") int timeout) {
        return userService.find(timeout);
    }

    @Autowired
    Router router;

    @RequestMapping("/gray/")
    public String gray(@RequestParam("ratio") int ratio) {
        ((GrayRouter)router).setGrayRatio(ratio);
        return "OK-new gray ratio is " + ratio;
    }

    public static void main(String[] args) {
        SpringApplication.run(WinrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumer_runner() {
        return x -> {
//              testAll();
        };
    }

    private void testAll() {
        // 常规int类型，返回User对象
        System.out.println("Case 1. >>===[常规int类型，返回User对象]===");
        User user = userService.findById(1);
        System.out.println("RPC result userService.findById(1) = " + user);

        // 测试方法重载，同名方法，参数不同
        System.out.println("Case 2. >>===[测试方法重载，同名方法，参数不同===");
        User user1 = userService.findById(1, "WinWang");
        System.out.println("RPC result userService.findById(1, \"WinWang\") = " + user1);

        // 测试返回字符串
        System.out.println("Case 3. >>===[测试返回字符串]===");
        System.out.println("userService.getName() = " + userService.getName());

        // 测试重载方法返回字符串
        System.out.println("Case 4. >>===[测试重载方法返回字符串]===");
        System.out.println("userService.getName(123) = " + userService.getName(123));

        // 测试local toString方法
        System.out.println("Case 5. >>===[测试local toString方法]===");
        System.out.println("userService.toString() = " + userService.toString());

        // 测试long类型
        System.out.println("Case 6. >>===[常规int类型，返回User对象]===");
        System.out.println("userService.getId(10) = " + userService.getId(10));

        // 测试long+float类型
        System.out.println("Case 7. >>===[测试long+float类型]===");
        System.out.println("userService.getId(10f) = " + userService.getId(10f));

        // 测试参数是User类型
        System.out.println("Case 8. >>===[测试参数是User类型]===");
        System.out.println("userService.getId(new User(100,\"WinWang\")) = " +
                userService.getId(new User(100,"Win")));


        System.out.println("Case 9. >>===[测试返回long[]]===");
        System.out.println(" ===> userService.getLongIds(): ");
        for (long id : userService.getLongIds()) {
            System.out.println(id);
        }

        System.out.println("Case 10. >>===[测试参数和返回值都是long[]]===");
        System.out.println(" ===> userService.getLongIds(): ");
        for (long id : userService.getIds(new int[]{4,5,6})) {
            System.out.println(id);
        }

        // 测试参数和返回值都是List类型
        System.out.println("Case 11. >>===[测试参数和返回值都是List类型]===");
        List<User> list = userService.getList(List.of(
                new User(100, "Win100"),
                new User(101, "Win101")));
        list.forEach(System.out::println);

        // 测试参数和返回值都是Map类型
        System.out.println("Case 12. >>===[测试参数和返回值都是Map类型]===");
        Map<String, User> map = new HashMap<>();
        map.put("A200", new User(200, "Win200"));
        map.put("A201", new User(201, "Win201"));
        userService.getMap(map).forEach(
                (k,v) -> System.out.println(k + " -> " + v)
        );

        System.out.println("Case 13. >>===[测试参数和返回值都是Boolean/boolean类型]===");
        System.out.println("userService.getFlag(false) = " + userService.getFlag(false));

        System.out.println("Case 14. >>===[测试参数和返回值都是User[]类型]===");
        User[] users = new User[]{
                new User(100, "Win100"),
                new User(101, "Win101")};
        Arrays.stream(userService.findUsers(users)).forEach(System.out::println);

        System.out.println("Case 15. >>===[测试参数为long，返回值是User类型]===");
        User userLong = userService.findById(10000L);
        System.out.println(userLong);

        System.out.println("Case 16. >>===[测试参数为boolean，返回值都是User类型]===");
        User user100 = userService.ex(false);
        System.out.println(user100);

        System.out.println("Case 17. >>===[测试服务端抛出一个RuntimeException异常]===");
        try {
            User userEx = userService.ex(true);
            System.out.println(userEx);
        } catch (RuntimeException e) {
            System.out.println(" ===> exception: " + e.getMessage());
        }

        System.out.println("Case 18. >>===[测试服务端抛出一个超时重试后成功的场景]===");
        // 超时设置【漏斗原则】
        // A 2000 -> B 1500 -> C 1200 -> D 1000
        // RpcContext.set // 需要考虑使用ThreadLocal绑定线程
        long start = System.currentTimeMillis();
        userService.find(100);
        userService.find(1100);
        System.out.println("userService.find take "
                + (System.currentTimeMillis() - start) + " ms");

        System.out.println("Case 19. >>===[测试通过Context跨消费者和提供者进行传参]===");
        String Key_Version = "rpc.version";
        String Key_Message = "rpc.message";
        RpcContext.setContextParameter(Key_Version, "v8");
        RpcContext.setContextParameter(Key_Message, "this is a v8 message");
        String version = userService.echoParameter(Key_Version);
        RpcContext.setContextParameter(Key_Version, "v9");
        RpcContext.setContextParameter(Key_Message, "this is a v9 message");
        String message = userService.echoParameter(Key_Message);
        System.out.println(" ===> echo parameter from c->p->c: " + Key_Version + " -> " + version);
        System.out.println(" ===> echo parameter from c->p->c: " + Key_Message + " -> " + message);
//        RpcContext.ContextParameters.get().clear();
    }

}
