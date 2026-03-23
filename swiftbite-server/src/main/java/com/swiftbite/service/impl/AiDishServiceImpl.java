package com.swiftbite.service.impl;

import com.swiftbite.constant.StatusConstant;
import com.swiftbite.entity.Dish;
import com.swiftbite.mapper.DishMapper;
import com.swiftbite.service.AiDishService;
import com.swiftbite.vo.AiQueryDishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * AI 菜品查询服务
 * 负责管理 Redis 中的菜品缓存，提供菜品查询功能
 */
@Service
@Slf4j
public class AiDishServiceImpl implements AiDishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String AI_DISH_CACHE_KEY = "ai:dish:all";

    /**
     * 更新 Redis 中的菜品缓存
     * 查询数据库中所有在售菜品并存储到 Redis
     */
    public void updateDishCache() {
        log.info("开始更新 AI 菜品缓存，时间：{}", java.time.LocalDateTime.now());

        try {
            // 1. 查询数据库中所有在售菜品
            List<AiQueryDishVO> dishes = dishMapper.aiQueyDish(StatusConstant.ENABLE);

            if (dishes == null || dishes.isEmpty()) {
                log.warn("未找到任何在售菜品");
                return;
            }

            // 2. 清空旧的缓存
            String cacheKey = AI_DISH_CACHE_KEY;
            Set<String> oldKeys = redisTemplate.keys(cacheKey + ":*");
            if (oldKeys != null && !oldKeys.isEmpty()) {
                redisTemplate.delete(oldKeys);
            }

            // 3. 将每个菜品信息存入 Redis
            for (AiQueryDishVO d : dishes) {
                String dishKey = cacheKey + ":" + d.getId();
                redisTemplate.opsForValue().set(dishKey, d);
            }

            // 4. 存储所有菜品 ID 列表
            redisTemplate.opsForValue().set(cacheKey + ":ids",
                    dishes.stream().map(AiQueryDishVO::getId).toList());

            log.info("成功更新 {} 个菜品到 Redis 缓存", dishes.size());

        } catch (Exception e) {
            log.error("更新菜品缓存失败", e);
            throw new RuntimeException("更新菜品缓存失败", e);
        }
    }

    /**
     * 根据关键词查询菜品信息
     * @param keyword 搜索关键词，为空时返回所有菜品
     * @return 格式化的菜品信息字符串
     */
    public String queryDishesByKeyword(String keyword) {
        log.info("AI 查询菜品信息，关键词：{}", keyword);

        try {
            // 1. 获取所有菜品 ID
            Object idsObj = redisTemplate.opsForValue().get(AI_DISH_CACHE_KEY + ":ids");
            if (idsObj == null) {
                return "暂无菜品信息，请稍后再试";
            }

            List<Integer> dishIds = new ArrayList<>();
            if (idsObj instanceof List) {
                dishIds = (List<Integer>) idsObj;
            } else if (idsObj instanceof Set) {
                dishIds = new ArrayList<>((Set<Integer>) idsObj);
            }

            // 2. 遍历所有菜品，根据关键词筛选
            List<String> matchedDishes = new ArrayList<>();
            for (Integer dishId : dishIds) {
                String dishKey = AI_DISH_CACHE_KEY + ":" + dishId;
                Object dishObj = redisTemplate.opsForValue().get(dishKey);

                if (dishObj != null) {
                    String dishInfo = formatDishInfoWithId(dishId, dishObj);

                    // 如果关键词为空或匹配菜品名称/描述
                    if (keyword == null || keyword.trim().isEmpty() ||
                            dishInfo.toLowerCase().contains(keyword.trim().toLowerCase())) {
                        matchedDishes.add(dishInfo);
                    }
                }
            }

            if (matchedDishes.isEmpty()) {
                return "未找到匹配的菜品信息";
            }

            // 3. 返回格式化的菜品列表（使用分隔符清晰区分每个菜品）
            String result = "找到 " + matchedDishes.size() + " 个菜品：" + String.join(" | ", matchedDishes);
            return result;

        } catch (Exception e) {
            log.error("AI 查询菜品失败", e);
            return "查询菜品信息失败：" + e.getMessage();
        }
    }

    /**
     * 格式化菜品信息（包含 dishId）
     * 格式：[dishId=1,name=宫保鸡丁，price=38.00,image=xxx.jpg,description=...]
     */
    private String formatDishInfoWithId(Integer dishId, Object dishObj) {
        if (dishObj instanceof AiQueryDishVO) {
            AiQueryDishVO dish = (AiQueryDishVO) dishObj;
            return String.format("[dishId=%d,name=%s,price=%.2f,image=%s,description=%s]",
                    dishId,
                    dish.getName(),
                    dish.getPrice(),
                    dish.getImage() != null ? dish.getImage() : "",
                    dish.getDescription() != null ? dish.getDescription() : "无");
        }
        if (dishObj instanceof Dish) {
            Dish dish = (Dish) dishObj;
            return String.format("[dishId=%d,name=%s,price=%.2f,image=%s,description=%s]",
                    dishId,
                    dish.getName(),
                    dish.getPrice().doubleValue(),
                    dish.getImage() != null ? dish.getImage() : "",
                    dish.getDescription() != null ? dish.getDescription() : "无");
        }
        return dishObj.toString();
    }

    /**
     * 获取所有菜品信息（不过滤）
     * @return 所有菜品的格式化信息
     */
    public String getAllDishes() {
        return queryDishesByKeyword(null);
    }
}
