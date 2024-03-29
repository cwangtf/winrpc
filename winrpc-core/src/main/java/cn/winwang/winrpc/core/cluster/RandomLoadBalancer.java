package cn.winwang.winrpc.core.cluster;

import cn.winwang.winrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/28 0:09
 */
public class RandomLoadBalancer<T> implements LoadBalancer<T> {

    Random random = new Random();

    @Override
    public T choose(List<T> providers) {
        if (providers == null || providers.size() == 0) return null;
        if (providers.size() == 1) return providers.get(0);
        return providers.get(random.nextInt(providers.size()));
    }
}
