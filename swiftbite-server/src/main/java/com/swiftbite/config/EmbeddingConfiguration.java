package com.swiftbite.config;

import com.swiftbite.properties.QdrantProperties;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

// @Configuration
public class EmbeddingConfiguration {

    @Autowired
    private QdrantProperties qdrantProperties;

    /*@Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(System.getenv("ALIYUN_BAILIAN_API_KEY")) // 从环境变量获取Key
                .modelName("text-embedding-v3") // 指定模型
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1") // 兼容OpenAI的API端点
                .timeout(Duration.ofSeconds(60)) // 设置超时
                .logRequests(true) // 开发时开启，方便调试
                .logResponses(true)
                .build();
    }*/

    // 暂时使用内存向量仓库，后续替换为Qdrant
    /*@Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return QdrantEmbeddingStore.builder()
                .host(qdrantProperties.getHost()) // Qdrant服务地址
                .port(qdrantProperties.getPort()) // gRPC端口
                .collectionName(qdrantProperties.getCollectionName())
                .build();
    }*/

}
