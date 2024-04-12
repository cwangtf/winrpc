package cn.winwang.winrpc.core.meta;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * 描述Provider的映射关系.
 *
 * @author winwang
 * @date 2024/3/24 14:55
 */
@Data
@Builder
public class ProviderMeta {

    Method method;

    String methodSign;

    Object serviceImpl;
}
