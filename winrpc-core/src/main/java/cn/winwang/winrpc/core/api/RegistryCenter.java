package cn.winwang.winrpc.core.api;

import cn.winwang.winrpc.core.meta.InstanceMeta;
import cn.winwang.winrpc.core.meta.ServiceMeta;
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
    void register(ServiceMeta service, InstanceMeta instance); // p

    void unregister(ServiceMeta service, InstanceMeta instance); // p

    // consumer侧
    List<InstanceMeta> fetchAll(ServiceMeta service); // c

    void subscribe(ServiceMeta service, ChangedListener listener);
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
        public void register(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public void unregister(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangedListener listener) {

        }
    }
}
