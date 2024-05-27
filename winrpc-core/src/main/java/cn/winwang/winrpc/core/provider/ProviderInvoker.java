package cn.winwang.winrpc.core.provider;

import cn.winwang.winrpc.core.api.RpcContext;
import cn.winwang.winrpc.core.api.RpcException;
import cn.winwang.winrpc.core.api.RpcRequest;
import cn.winwang.winrpc.core.api.RpcResponse;
import cn.winwang.winrpc.core.governance.SlidingTimeWindow;
import cn.winwang.winrpc.core.meta.ProviderMeta;
import cn.winwang.winrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.winwang.winrpc.core.api.RpcException.ExceedLimitEx;

/**
 * invoke the service methods in provider.
 *
 * @author winwang
 * @date 2024/4/7 23:33
 */
@Slf4j
public class ProviderInvoker {

    private MultiValueMap<String, ProviderMeta> skeleton;

    private final int trafficControl;// = 20;

    final Map<String, SlidingTimeWindow> windows = new HashMap<>();
    final Map<String, String> metas;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.skeleton = providerBootstrap.getSkeleton();
        this.metas = providerBootstrap.getProviderProperties().getMetas();
        this.trafficControl = Integer.parseInt(metas.getOrDefault("tc", "20"));
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        log.debug(" ===> ProviderInvoker.invoke(request:{})", request);
        if(!request.getParams().isEmpty()) {
            request.getParams().forEach(RpcContext::setContextParameter);
        }
        RpcResponse<Object> rpcResponse = new RpcResponse();
        String service = request.getService();
        synchronized (windows) {
            SlidingTimeWindow window = windows.computeIfAbsent(service, k -> new SlidingTimeWindow());
            if (window.calcSum() >= trafficControl) {
                System.out.println(window);
                throw new RpcException("service " + service + " invoked in 30s/[" +
                        window.getSum() + "] larger than tpsLimit = " + trafficControl, ExceedLimitEx);
            } else {
                window.record(System.currentTimeMillis());
                log.debug("service {} in window with {}", service, window.getSum());
            }
        }

        List<ProviderMeta> providerMetas = skeleton.get(service);
        try {
            ProviderMeta meta = findProviderMeta(providerMetas, request.getMethodSign());
            Method method = meta.getMethod();
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes(), method.getGenericParameterTypes());
            Object result = method.invoke(meta.getServiceImpl(), args);
            rpcResponse.setStatus(true);
            rpcResponse.setData(result);
        } catch (InvocationTargetException e) {
            rpcResponse.setEx(new RpcException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException | IllegalArgumentException e) {
            rpcResponse.setEx(new RpcException(e.getMessage()));
        } finally {
            RpcContext.ContextParameters.get().clear(); // 防止内存泄露和上下文污染
        }
        log.debug(" ===> ProviderInvoker.invoke() = {}", rpcResponse);

        return rpcResponse;
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes, Type[] getGenericParameterTypes) {
        if(args == null || args.length == 0) return args;
        Object[] actual = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            actual[i] = TypeUtils.castGeneric(args[i], parameterTypes[i], getGenericParameterTypes[i]);
        }
        return actual;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        Optional<ProviderMeta> optional = providerMetas.stream().filter(x -> x.getMethodSign().equals(methodSign)).findFirst();
        return optional.orElse(null);
    }

}
