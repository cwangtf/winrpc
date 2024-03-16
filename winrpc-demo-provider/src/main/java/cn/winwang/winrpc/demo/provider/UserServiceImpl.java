package cn.winwang.winrpc.demo.provider;

import cn.winwang.winrpc.core.annotation.WinProvider;
import cn.winwang.winrpc.demo.api.User;
import cn.winwang.winrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

/**
 * Description for this class.
 *
 * @author  winwang
 * @date  2024/3/16 14:37
 */
@Component
@WinProvider
public class UserServiceImpl implements UserService {
    @Override
    public User findById(int id) {
        return new User(id, "Win-" + System.currentTimeMillis());
    }
}
