package cn.winwang.winrpc.core.transport;

import cn.winwang.winrpc.core.meta.InstanceMeta;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/6/10 17:12
 */
@RestController()
@RequestMapping("/registry")
public class MockWinRegistry {

    public MockWinRegistry() {
        System.out.println(" =====>>>>>>>>> Mock WinRegistry start ... ");
        System.out.println(" =====>>>>>>>>> MockK WRegistry start ... ");
        System.out.println(" =====>>>>>>>>> MockKK Registry start ... ");
    }

    public final static MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap<>();
    public final static AtomicLong VERSION = new AtomicLong(0);

    @RequestMapping("/reg")
    public InstanceMeta reg(@RequestParam("service") String service, @RequestBody InstanceMeta instance) {
        REGISTRY.add(service, instance);
        VERSION.getAndIncrement();
        return instance;
    }

    @RequestMapping("/unreg")
    public InstanceMeta unreg(@RequestParam("service") String service, @RequestBody InstanceMeta instance) {
        REGISTRY.remove(service, instance);
        VERSION.getAndIncrement();
        return instance;
    }

    @RequestMapping("/findAll")
    public List<InstanceMeta> findAllInstances(@RequestParam("service") String service) {
        return REGISTRY.get(service);
    }

    @RequestMapping("/renew")
    public long renew(@RequestParam("service") String service, @RequestBody InstanceMeta instance) {
        return System.currentTimeMillis();
    }

    @RequestMapping("/renews")
    public long renews(@RequestParam("services") String services, @RequestBody InstanceMeta instance) {
        return System.currentTimeMillis();
    }

    @RequestMapping("/version")
    public long version(@RequestParam("service") String service) {
        return VERSION.get();
    }

    @RequestMapping("/versions")
    public Map<String, Long> versions(@RequestParam("services") String services) {
        return Arrays.stream(services.split(",")).collect(Collectors.toMap(k -> k, v -> VERSION.get()));
    }

}
