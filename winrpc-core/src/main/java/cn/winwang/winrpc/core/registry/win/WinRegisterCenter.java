package cn.winwang.winrpc.core.registry.win;

import cn.winwang.winrpc.core.api.RegistryCenter;
import cn.winwang.winrpc.core.meta.InstanceMeta;
import cn.winwang.winrpc.core.meta.ServiceMeta;
import cn.winwang.winrpc.core.registry.ChangedListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * implementation for win registry center.
 *
 * @author winwang
 * @date 2024/6/2 22:48
 */
@Slf4j
public class WinRegisterCenter implements RegistryCenter {

    @Value("${winregistry.servers}")
    private String servers;

    @Override
    public void start() {
        log.info(" ===>>> [WinRegistry] : start with servers : {}", servers);
    }

    @Override
    public void stop() {
        log.info(" ===>>> [WinRegistry] : stop with servers : {}", servers);
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ===>>> [WinRegistry] : registry instance {} for {}", instance, service);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {

    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        return null;
    }

    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {

    }
}
