package cn.winwang.winrpc.core.registry;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/4/6 23:00
 */
public interface ChangedListener {
    void fire(Event event);
}
