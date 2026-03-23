package com.swiftbite.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimeoutVO {
    // 订单id
    private Long id;
    // 下单时间
    private LocalDateTime orderTime;
    //订单取消原因
    private String cancelReason;
}
