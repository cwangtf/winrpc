package cn.winwang.winrpc.core.api;

import java.util.List;

/**
 * 路由器.
 *
 * @author winwang
 * @date 2024/3/26 23:37
 */
public interface Router<T> {

    List<T> route(List<T> providers);

    Router Default = p -> p;

}
