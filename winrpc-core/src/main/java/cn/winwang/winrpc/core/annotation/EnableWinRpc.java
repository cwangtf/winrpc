package cn.winwang.winrpc.core.annotation;

import cn.winwang.winrpc.core.config.ConsumerConfig;
import cn.winwang.winrpc.core.config.ProviderConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/5/26 18:28
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({ProviderConfig.class, ConsumerConfig.class})
public @interface EnableWinRpc {
}
