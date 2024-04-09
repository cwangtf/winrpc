package cn.winwang.winrpc.core.consumer;

import cn.winwang.winrpc.core.annotation.WinConsumer;
import cn.winwang.winrpc.core.api.LoadBalancer;
import cn.winwang.winrpc.core.api.RegistryCenter;
import cn.winwang.winrpc.core.api.Router;
import cn.winwang.winrpc.core.api.RpcContext;
import cn.winwang.winrpc.core.registry.ChangedListener;
import cn.winwang.winrpc.core.registry.Event;
import cn.winwang.winrpc.core.util.MethodUtils;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/18 23:19
 */
@Data
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext applicationContext;

    Environment environment;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {

        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);

        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass(), WinConsumer.class);
            fields.stream().forEach(f -> {
                System.out.println(" ===> " + f.getName());
                try {
                    Class<?> service = f.getType();
                    String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        consumer = createFromRegistry(service, context, rc);
                    }
                    f.setAccessible(true);
                    f.set(bean, consumer);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private Object createFromRegistry(Class<?> service, RpcContext context, RegistryCenter rc) {
        String serviceName = service.getCanonicalName();
        List<String> providers = mapUrl(rc.fetchAll(serviceName));
        System.out.println(" ===> map to providers: ");
        providers.forEach(System.out::println);

        rc.subscribe(serviceName, event -> {
            providers.clear();
            providers.addAll(mapUrl(event.getData()));
        });

        return createConsumer(service, context, providers);
    }

    private List<String> mapUrl(List<String> nodes) {
        return nodes.stream()
                .map(x -> "http://" + x.replace('_', ':')).collect(Collectors.toList());
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<String> providers) {
        // Java 动态代理
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service}, new WinInvocationHandler(service, context, providers));
    }

}
