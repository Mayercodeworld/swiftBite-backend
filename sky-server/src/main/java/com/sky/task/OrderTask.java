package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.util.List;


/**
 * 定时任务类，定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    /**
     * 处理支付超时的订单，每分钟检查
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟触发异常
    public void processTimeoutOrder() {
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Integer> ids = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time); // 查询所有超时订单
        // log.info("{}", ids);

        if(!ids.isEmpty()){
            /*
            不能删除，只能修改status的值
            orderMapper.batchDelete(ids);
            */
            Orders order = Orders.builder()
                    .status(Orders.CANCELLED)
                    .cancelTime(LocalDateTime.now())
                    .cancelReason("订单超时，自动取消")
                    .build();

            orderMapper.updateStatus(ids, order);
        }
    }

    /**
     * 处理一直在派送中的订单(24h状态未更新)
     */

    @Scheduled(cron = "0 0 1 * * ?") // 每天1点触发一次
    public void processDeliveryOrder() {
        log.info("定时处理派送中的订单：{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().minusHours(24); // 当前1点触发前一天凌晨
        List<Integer> ids = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        Orders order = Orders.builder()
                .status(Orders.COMPLETED)
                .build();

        if(!ids.isEmpty()){
            orderMapper.updateStatus(ids, order);
        }
    }
}
