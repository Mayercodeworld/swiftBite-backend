package com.swiftbite.task;

import com.swiftbite.mapper.DishMapper;
import com.swiftbite.service.RagIngestionService;
import com.swiftbite.vo.AiQueryDishVO;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 定时更新向量数据库
 */
@Component
@Slf4j
public class RagTask {

    @Autowired
    private RagIngestionService ragIngestionService;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 定时更新向量数据库（查询商品的名称）
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void ingest() {
        try {
            log.info("开始从数据库查询菜品数据以更新向量数据库");

            // 1. 从数据库查询所有在售菜品的名称和描述
            List<AiQueryDishVO> dishes = dishMapper.aiQueyDish(1);

            if (dishes == null || dishes.isEmpty()) {
                log.warn("未查询到任何在售菜品数据");
                return;
            }

            // 2. 将菜品数据转换为Document对象列表
            List<Document> documents = new ArrayList<>();
            for (AiQueryDishVO dish : dishes) {
                String content = String.format("菜品名称：%s\n菜品描述：%s",
                        dish.getName(),
                        dish.getDescription() != null ? dish.getDescription() : "无描述");

                Document document = Document.from(content);
                // 添加元数据，便于后续检索时使用
                document.metadata().put("dish_id", dish.getId());
                document.metadata().put("dish_name", dish.getName());
                document.metadata().put("type", "dish");

                documents.add(document);
            }

            // 3. 将文档列表写入向量数据库
            ragIngestionService.ingestDocuments(documents);
            log.info("成功将 {} 个菜品数据写入向量数据库", documents.size());

        } catch (Exception e) {
            log.error("更新向量数据库失败", e);
        }
    }

    // @Scheduled(cron = "0/5 * * * * ?")
//    public void test() {
//        String queryText = "测试向量生成";
//        try {
//            Response<Embedding> embeddingResponse = embeddingModel.embed(queryText);
//            Embedding embedding = embeddingResponse.content();
//            float[] vector = embedding.vector(); // 注意：这里返回的是 float[]
//
//            System.out.println("Query text: " + queryText);
//            // 修正：使用 .length 而不是 .size()
//            System.out.println("Vector size: " + (vector != null ? vector.length : "null"));
//
//            if (vector != null && vector.length > 0) { // 修正：使用 .length > 0
//                // 打印前几个元素（如果数组长度小于5，则打印全部）
//                int printLength = Math.min(5, vector.length);
//                StringBuilder sb = new StringBuilder("[");
//                for (int i = 0; i < printLength; i++) {
//                    sb.append(vector[i]);
//                    if (i < printLength - 1) sb.append(", ");
//                }
//                sb.append("]");
//                System.out.println("First few elements: " + sb.toString());
//            } else {
//                System.err.println("ERROR: Generated vector is null or empty!");
//                // 这里可以抛出异常或采取其他措施，避免继续执行检索
//            }
//        } catch (Exception e) {
//            System.err.println("ERROR: Failed to generate embedding for query: " + queryText);
//            e.printStackTrace();
//        }
//    }
}
