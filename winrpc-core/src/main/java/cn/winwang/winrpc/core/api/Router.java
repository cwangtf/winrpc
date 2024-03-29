package cn.winwang.winrpc.core.api;

import java.util.List;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/26 23:37
 */
public interface Router<T> {

    List<T> route(List<T> providers);

    Router Default = p -> p;

}
