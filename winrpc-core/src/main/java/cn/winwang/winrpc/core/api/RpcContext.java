package cn.winwang.winrpc.core.api;

import cn.winwang.winrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/29 23:31
 */
@Data
public class RpcContext {

    List<Filter> filterList;

    Router<InstanceMeta> router;

    LoadBalancer<InstanceMeta> loadBalancer;

}
