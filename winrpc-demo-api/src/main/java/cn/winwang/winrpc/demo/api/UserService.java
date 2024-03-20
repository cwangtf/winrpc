package cn.winwang.winrpc.demo.api;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/14 23:51
 */
public interface UserService {

    User findById(int id);

    int getId(int id);

    String getName();

}
