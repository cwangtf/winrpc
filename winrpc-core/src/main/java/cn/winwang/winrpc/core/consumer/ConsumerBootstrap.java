package cn.winwang.winrpc.core.consumer;

import cn.winwang.winrpc.core.annotation.WinConsumer;
import cn.winwang.winrpc.core.api.RegistryCenter;
import cn.winwang.winrpc.core.api.RpcContext;
import cn.winwang.winrpc.core.meta.InstanceMeta;
import cn.winwang.winrpc.core.meta.ServiceMeta;
import cn.winwang.winrpc.core.util.MethodUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消费者启动类.
 *
 * @author winwang
 * @date 2024/3/18 23:19
 */
@Data
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext applicationContext;

    Environment environment;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {

        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        RpcContext context = applicationContext.getBean(RpcContext.class);

        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass(), WinConsumer.class);
            fields.stream().forEach(f -> {
                Class<?> service = f.getType();
                String serviceName = service.getCanonicalName();
                log.info(" ===> " + f.getName());
                try {
                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        consumer = createFromRegistry(service, context, rc);
                        stub.put(serviceName, consumer);
                    }
                    f.setAccessible(true);
                    f.set(bean, consumer);
                } catch (Exception ex) {
                    // ignore and print it
                    log.warn(" ==> Field[{}.{}] create consumer failed.", serviceName, f.getName());
                    log.error("Ignore and print it as: ", ex);
                }
            });
        }
    }

    private Object createFromRegistry(Class<?> service, RpcContext context, RegistryCenter rc) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(context.param("app.id")).namespace(context.param("app.namespace"))
                .env(context.param("app.env")).name(service.getCanonicalName()).build();
        List<InstanceMeta> providers = rc.fetchAll(serviceMeta);
        log.info(" ===> map to providers: ");
        providers.forEach(System.out::println);

        rc.subscribe(serviceMeta, event -> {
            providers.clear();
            providers.addAll(event.getData());
        });

        return createConsumer(service, context, providers);
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        // Java 动态代理
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service}, new WinInvocationHandler(service, context, providers));
    }

}
