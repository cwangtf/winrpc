package cn.winwang.winrpc.demo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class.
 *
 * @author winwang
 * @date 2024/3/16 17:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    Long id;

    Float amount;
}
