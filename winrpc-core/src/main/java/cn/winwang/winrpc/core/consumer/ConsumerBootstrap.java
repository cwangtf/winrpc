package cn.winwang.winrpc.core.consumer;

import cn.winwang.winrpc.core.annotation.WinConsumer;
import cn.winwang.winrpc.core.api.LoadBalancer;
import cn.winwang.winrpc.core.api.Router;
import cn.winwang.winrpc.core.api.RpcContext;
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

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);

        String urls = environment.getProperty("winrpc.providers", "");
        if (Strings.isEmpty(urls)) {
            System.out.println("winrpc.providers is empty");
        }
        String[] providers = urls.split(",");


        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = findAnnotatedField(bean.getClass());
            fields.stream().forEach(f -> {
                System.out.println(" ===> " + f.getName());
                try {
                    Class<?> service = f.getType();
                    String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        consumer = createConsumer(service, context, List.of(providers));
                    }
                    f.setAccessible(true);
                    f.set(bean, consumer);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<String> providers) {
        // Java 动态代理
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service}, new WinInvocationHandler(service, context, providers));
    }

    private List<Field> findAnnotatedField(Class<?> aClass) {
        List<Field> result = new ArrayList<>();
        while (aClass != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(WinConsumer.class)) {
                    result.add(f);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return result;
    }

}
