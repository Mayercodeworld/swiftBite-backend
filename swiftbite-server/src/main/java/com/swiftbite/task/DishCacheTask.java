package com.swiftbite.task;

import com.swiftbite.service.AiDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时更新 Redis 中的菜品信息，供 AI 查询使用
 */
@Component
@Slf4j
public class DishCacheTask {

    @Autowired
    private AiDishService aiDishService;

    /**
     * 每隔 5 分钟更新一次 Redis 中的菜品信息
     * 只在商店营业时更新（status=1 的菜品）
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateDishCache() {
        aiDishService.updateDishCache();
    }

}
