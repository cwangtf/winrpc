package cn.winwang.winrpc.demo.provider;

import cn.winwang.winrpc.core.annotation.WinProvider;
import cn.winwang.winrpc.core.api.RpcContext;
import cn.winwang.winrpc.demo.api.User;
import cn.winwang.winrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * User Service Impl.
 *
 * @author  winwang
 * @date  2024/3/16 14:37
 */
@Component
@WinProvider
public class UserServiceImpl implements UserService {

    @Autowired
    Environment environment;

    @Override
    public User findById(int id) {
        return new User(id, "Win-V1-"
                + environment.getProperty("server.port")
                + "_" + System.currentTimeMillis());
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
    public User[] findUsers(User[] users) {
        return users;
    }

    @Override
    public List<User> getList(List<User> userList) {
//        System.out.println(userList.size());
//        System.out.println(userList.getClass());
//        userList.forEach(x -> System.out.println("x=>"+x.getClass()));
        User[] users = userList.toArray(new User[userList.size()]);
        System.out.println(" ==> userList.toArray()[] = ");
        Arrays.stream(users).forEach(System.out::println);
        userList.add(new User(2024,"WinWang2024"));
        return userList;
    }

    @Override
    public Map<String, User> getMap(Map<String, User> userMap) {
        userMap.values().forEach(x -> System.out.println(x.getClass()));
        User[] users = userMap.values().toArray(new User[userMap.size()]);
        System.out.println(" ==> userMap.values().toArray()[] = ");
        Arrays.stream(users).forEach(System.out::println);
        userMap.put("A2024", new User(2024,"WinWang2024"));
        return userMap;
    }

    @Override
    public Boolean getFlag(boolean flag) {
        return !flag;
    }

    @Override
    public User findById(long id) {
        return new User(Long.valueOf(id).intValue(), "WinWang");
    }

    @Override
    public User ex(boolean flag) {
        if(flag) throw new RuntimeException("just throw an exception");
        return new User(100, "Win100");
    }

    String timeoutPorts = "8081,8094";

    @Override
    public User find(int timeout) {
        String port = environment.getProperty("server.port");
        if (Arrays.stream(timeoutPorts.split(",")).anyMatch(port::equals)) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new User(1001, "Win1001-" + port);
    }

    public void setTimeoutPorts(String timeoutPorts) {
        this.timeoutPorts = timeoutPorts;
    }

    @Override
    public String echoParameter(String key) {
        System.out.println(" ====>> RpcContext.ContextParameters: ");
        RpcContext.ContextParameters.get().forEach((k,v)-> System.out.println(k+" -> " +v));
        return RpcContext.getContextParameter(key);
    }
}
