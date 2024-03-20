package cn.winwang.winrpc.demo.provider;

import cn.winwang.winrpc.core.annotation.WinProvider;
import cn.winwang.winrpc.demo.api.Order;
import cn.winwang.winrpc.demo.api.OrderService;
import org.springframework.stereotype.Component;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/16 18:01
 */
@Component
@WinProvider
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer id) {

        if (id == 404) {
            throw new RuntimeException("404 exception");
        }

        return new Order(id.longValue(), 15.6f);
    }
}
