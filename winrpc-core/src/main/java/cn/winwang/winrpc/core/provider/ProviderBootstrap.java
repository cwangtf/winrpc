package cn.winwang.winrpc.core.provider;

import cn.winwang.winrpc.core.annotation.WinProvider;
import cn.winwang.winrpc.core.api.RegistryCenter;
import cn.winwang.winrpc.core.meta.InstanceMeta;
import cn.winwang.winrpc.core.meta.ProviderMeta;
import cn.winwang.winrpc.core.meta.ServiceMeta;
import cn.winwang.winrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/16 17:15
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {

    ApplicationContext applicationContext;
    RegistryCenter rc;

    // 桩子
    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    private InstanceMeta instance;

    @Value("${server.port}")
    private String port;

    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;

    @PostConstruct // init-method
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(WinProvider.class);
        rc = applicationContext.getBean(RegistryCenter.class);
        providers.forEach((x,y) -> System.out.println(x));
        providers.values().forEach(this::getInterface);
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = InstanceMeta.http(ip, Integer.valueOf(port));
        rc.start();
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        System.out.println(" ===> unregister all services.");
        skeleton.keySet().forEach(this::unregisterService);
        rc.stop();
    }

    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app).namespace(namespace).env(env).name(service).build();
        rc.register(serviceMeta, instance);
    }

    private void unregisterService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app).namespace(namespace).env(env).name(service).build();
        rc.unregister(serviceMeta, instance);
    }

    private void getInterface(Object impl) {
        Arrays.stream(impl.getClass().getInterfaces()).forEach(
                service -> {
                    Method[] methods = service.getMethods();
                    for (Method method : methods) {
                        if (MethodUtils.checkLocalMethod(method)) {
                            continue;
                        }
                        createProvider(service, impl, method);
                    }
                }
        );
    }

    private void createProvider(Class<?> service, Object impl, Method method) {
        ProviderMeta providerMeta = ProviderMeta.builder().method(method)
                .serviceImpl(impl).methodSign(MethodUtils.methodSign(method)).build();
        System.out.println(" create a provider:" + providerMeta);
        skeleton.add(service.getCanonicalName(), providerMeta);
    }

}
