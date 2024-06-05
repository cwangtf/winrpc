package cn.winwang.winrpc.core.registry.win;

import cn.winwang.winrpc.core.api.RegistryCenter;
import cn.winwang.winrpc.core.consumer.HttpInvoker;
import cn.winwang.winrpc.core.meta.InstanceMeta;
import cn.winwang.winrpc.core.meta.ServiceMeta;
import cn.winwang.winrpc.core.registry.ChangedListener;
import cn.winwang.winrpc.core.registry.Event;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        executor = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void stop() {
        log.info(" ===>>> [WinRegistry] : stop with servers : {}", servers);
        executor.shutdown();
        try {
            executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {

        }

    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ===>>> [WinRegistry] : registry instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/reg?service=" + service.toPath(), InstanceMeta.class);
        log.info(" ===>>> [WinRegistry] : registered {}", instance);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ===>>> [WinRegistry] : unregister instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/unreg?service=" + service.toPath(), InstanceMeta.class);
        log.info(" ===>>> [WinRegistry] : unregistered {}", instance);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ===>>> [WinRegistry] : find all instance for {}", service);
        List<InstanceMeta> instances = HttpInvoker.httpGet(servers + "/findAll?service=" + service.toPath(), new TypeReference<List<InstanceMeta>>() {
        });
        log.info(" ===>>> [WinRegistry] : findAll = {}", instances);
        return null;
    }

    Map<String, Long> VERSIONS = new HashMap<>();
    ScheduledExecutorService executor = null;

    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        executor.scheduleWithFixedDelay(() -> {
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            Long newVersion = HttpInvoker.httpGet(servers + "/version?service=" + service.toPath(), Long.class);
            log.info(" ===>>> [WinRegistry] : version = {}, newVersion = {}", version, newVersion);
            if (newVersion > version) {
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                VERSIONS.put(service.toPath(), newVersion);
            }
        }, 1000, 5000, TimeUnit.MILLISECONDS);
    }
}
