package com.swiftbite.ai;

import com.swiftbite.service.AiDishService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AiTools {

    @Autowired
    private AiDishService aiDishService;

    /**
     * 菜品信息查询工具
     *
     * 功能说明：
     * 1. 从 Redis 缓存中查询当前商店正在销售的菜品信息，包含字段：id（菜品 ID）、name（名称）、price（价格）、
     *    image（图片 URL）、description（描述）
     * 2. 支持关键词模糊匹配：当用户提供关键词时，会在菜品名称和描述中进行匹配
     * 3. 返回格式化的菜品列表，每条菜品包含完整信息（ID、名称、价格、图片链接、描述）
     *
     * 使用场景：
     * - 用户询问"有什么好吃的推荐"时，调用此工具获取所有菜品
     * - 用户询问"有辣的菜吗"或"推荐些清淡的菜品"时，用关键词"辣"或"清淡"进行筛选
     * - 用户想了解今日特供、招牌菜等，可通过关键词匹配描述信息
     *
     * 参数说明：
     * - keyword: 用户的搜索关键词，可以是口味（辣、甜、酸）、食材（牛肉、鸡肉）、烹饪方式（炒、蒸、煮）等
     *   如果用户没有特定偏好，传入空字符串将返回所有在售菜品
     *
     * 返回值说明：
     * - 成功时返回格式化的菜品信息字符串，格式为："找到 N 个菜品：【菜名】价格：X.XX 元，描述：xxx，ID：xxx"
     * - 无匹配结果时返回"未找到匹配的菜品信息"
     * - 缓存为空时返回"暂无菜品信息，请稍后再试"
     *
     * 注意事项：
     * - 仅查询状态为"起售"（status=1）的菜品，停售菜品不会返回
     * - 数据每 5 分钟自动更新一次，确保信息时效性
     */
    @Tool(
            name = "queryDishInfo",
            value = """
                    Query dish information from the restaurant menu. Returns dishes with 
                    structured format: [dishId=X,name=Y,price=Z,image=URL,description=...].
                    Supports keyword filtering by taste, ingredients, or cooking method.
                    Each dish includes ID for cart operations.
                    """
    )
    public String queryDish(@P(value = "the keyword to query", required = false) String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = "";
        }
        log.info("AI 工具被调用，关键词：{}", keyword);
        String result = aiDishService.queryDishesByKeyword(keyword);

        // 确保返回非空结果，避免模型调用失败
        if (result == null || result.trim().isEmpty()) {
            return "No dishes found matching your criteria. Please try different keywords or ask for general recommendations.";
        }

        log.info("AI 工具返回结果：{}", result);
        return result;
    }

}
