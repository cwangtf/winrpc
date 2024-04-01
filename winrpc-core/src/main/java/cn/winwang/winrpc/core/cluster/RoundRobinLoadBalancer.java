package cn.winwang.winrpc.core.cluster;

import cn.winwang.winrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/28 0:18
 */
public class RoundRobinLoadBalancer<T> implements LoadBalancer<T> {

    AtomicInteger index = new AtomicInteger(0);
    @Override
    public T choose(List<T> providers) {
        if (providers == null || providers.isEmpty()) return null;
        if (providers.size() == 1) return providers.get(0);
        return providers.get((index.getAndIncrement()&0x7fffffff) % providers.size());
    }
}
