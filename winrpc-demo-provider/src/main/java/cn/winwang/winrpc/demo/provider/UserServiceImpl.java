package cn.winwang.winrpc.demo.provider;

import cn.winwang.winrpc.core.annotation.WinProvider;
import cn.winwang.winrpc.demo.api.User;
import cn.winwang.winrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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

    @Override
    public User findById(int id, String name) {
        return new User(id, "Win-" + name + "_" + System.currentTimeMillis());
    }

    @Override
    public long getId(long id) {
        return id;
    }

    @Override
    public long getId(User user) {
        return user.getId().longValue();
    }

    @Override
    public long getId(float id) {
        return 1L;
    }

    @Override
    public String getName() {
        return "WinWang";
    }

    @Override
    public String getName(int id) {
        return "Chen-" + id;
    }

    @Override
    public int[] getIds() {
        return new int[]{100,200,300};
    }

    @Override
    public long[] getLongIds() {
        return new long[]{1,2,3};
    }

    @Override
    public int[] getIds(int[] ids) {
        return ids;
    }

    @Override
    public List<User> getList(List<User> userList) {
        return userList;
    }

    @Override
    public Map<String, User> getMap(Map<String, User> userMap) {
        return userMap;
    }
}
