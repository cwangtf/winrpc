package cn.winwang.winrpc.core.registry;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/4/6 23:06
 */
@Data
@AllArgsConstructor
public class Event {
    List<String> data;
}
