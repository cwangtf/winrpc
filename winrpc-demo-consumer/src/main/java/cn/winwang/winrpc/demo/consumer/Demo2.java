package cn.winwang.winrpc.demo.consumer;

import cn.winwang.winrpc.core.annotation.WinConsumer;
import cn.winwang.winrpc.demo.api.User;
import cn.winwang.winrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/20 1:03
 */
@Component
public class Demo2 {

    @WinConsumer
    UserService userService2;

    public void test() {
        User user = userService2.findById(100);
        System.out.println(user);
    }

}
