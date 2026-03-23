package com.swiftbite.service;

/**
 * AI 菜品查询服务
 * 负责管理 Redis 中的菜品缓存，提供菜品查询功能
 */
public interface AiDishService {

    /**
     * 更新 Redis 中的菜品缓存
     * 查询数据库中所有在售菜品并存储到 Redis
     */
    void updateDishCache();

    /**
     * 根据关键词查询菜品信息
     * @param keyword 搜索关键词，为空时返回所有菜品
     * @return 格式化的菜品信息字符串
     */
    String queryDishesByKeyword(String keyword);

}
