package cn.winwang.winrpc.core.api;

import cn.winwang.winrpc.core.meta.InstanceMeta;
import cn.winwang.winrpc.core.registry.ChangedListener;

import java.util.List;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/4/2 0:39
 */
public interface RegistryCenter {

    void start(); // p/c

    void stop(); // p/c

    // provider侧
    void register(String service, InstanceMeta instance); // p

    void unregister(String service, InstanceMeta instance); // p

    // consumer侧
    List<InstanceMeta> fetchAll(String service); // c

    void subscribe(String service, ChangedListener listener);
    // void heartbeat();

    class StaticRegistryCenter implements RegistryCenter {

        List<InstanceMeta> providers;

        public StaticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, InstanceMeta instance) {

        }

        @Override
        public void unregister(String service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangedListener listener) {

        }
    }
}
