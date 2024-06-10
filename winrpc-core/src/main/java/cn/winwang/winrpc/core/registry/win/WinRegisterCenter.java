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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final String REG_PATH = "/reg";
    private static final String UNREG_PATH = "/unreg";
    private static final String FINDALL_PATH = "/findAll";
    private static final String VERSION_PATH = "/version";
    private static final String RENEWS_PATH = "/renews";

    @Value("${winregistry.servers}")
    private String servers;

    Map<String, Long> VERSIONS = new HashMap<>();
    MultiValueMap<InstanceMeta, ServiceMeta> RENEWS = new LinkedMultiValueMap<>();
    WinHealthChecker healthChecker = new WinHealthChecker();


    @Override
    public void start() {
        log.info(" ===>>> [WinRegistry] : start with servers : {}", servers);
        healthChecker.start();
        providerCheck();
    }

    @Override
    public void stop() {
        log.info(" ===>>> [WinRegistry] : stop with servers : {}", servers);
        healthChecker.stop();
    }

    private void gracefulShutdown(ScheduledExecutorService executorService) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (!executorService.isTerminated()) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {

        }
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ===>>> [WinRegistry] : registry instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), regPath(service), InstanceMeta.class);
        log.info(" ===>>> [WinRegistry] : registered {}", instance);
        RENEWS.add(instance, service);
    }



    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ===>>> [WinRegistry] : unregister instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), unregPath(service), InstanceMeta.class);
        log.info(" ===>>> [WinRegistry] : unregistered {}", instance);
        RENEWS.remove(instance, service);
    }



    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ===>>> [WinRegistry] : find all instance for {}", service);
        List<InstanceMeta> instances = HttpInvoker.httpGet(findAllPath(service), new TypeReference<List<InstanceMeta>>() {
        });
        log.info(" ===>>> [WinRegistry] : findAll = {}", instances);
        return null;
    }

    public void providerCheck() {
        healthChecker.providerCheck(() -> {
            RENEWS.keySet().stream().forEach(
                    instance -> {

                        Long timestamp = HttpInvoker.httpPost(JSON.toJSONString(instance), renewsPath(RENEWS.get(instance)), Long.class);
                        log.info(" ====> [WinRegistry] : renew instance {} at {}", instance, timestamp);
                    }
            );
        });
    }


    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        healthChecker.consumerCheck(() -> {
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            Long newVersion = HttpInvoker.httpGet(versionPath(service), Long.class);
            log.info(" ===>>> [WinRegistry] : version = {}, newVersion = {}", version, newVersion);
            if (newVersion > version) {
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                VERSIONS.put(service.toPath(), newVersion);
            }
        });
    }

    @NotNull
    private String regPath(ServiceMeta service) {
        return path(REG_PATH, service);
    }

    @NotNull
    private String unregPath(ServiceMeta service) {
        return path(UNREG_PATH, service);
    }

    @NotNull
    private String findAllPath(ServiceMeta service) {
        return path(FINDALL_PATH, service);
    }

    private String versionPath(ServiceMeta service) {
        return path("version", service);
    }

    private String path(String context, ServiceMeta service) {
        return servers + context + "?service=" + service.toPath();
    }

    private String renewsPath(List<ServiceMeta> serviceList) {
        return path(RENEWS_PATH, serviceList);
    }

    private String path(String context, List<ServiceMeta> serviceList) {
        StringBuffer sb = new StringBuffer();
        for (ServiceMeta service : serviceList) {
            sb.append(service.toPath()).append(",");
        }
        String services = sb.toString();
        if (services.endsWith(",")) services = services.substring(0, services.length() - 1);
        log.info(" ====> [WinRegistry] : renew instance for {}", services);
        return servers + context + "?services=" + services;
    }
}
