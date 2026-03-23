package com.swiftbite.config;

import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RagConfiguration {

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

    @Resource
    private EmbeddingModel embeddingModel;

    // 向量数据注入器：将查询数据注入到Qdrant向量库中
    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor() {
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
//                .documentSplitter(new DocumentByParagraphSplitter(1000, 200))
//                .textSegmentTransformer(segment -> {
//                    String source = segment.metadata().getString("file_name");
//                    if (source == null) {
//                        source = segment.metadata().getString("source");
//                    }
//                    String enhancedText = "【来源：" + (source != null ? source : "unknown") + "】\n" + segment.text();
//                    return TextSegment.from(enhancedText, segment.metadata());
//                })
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        log.info("向量数据注入器创建完成。向量模型：{}, 存储类型：{}",
                embeddingModel.getClass().getSimpleName(),
                embeddingStore.getClass().getSimpleName());

        return ingestor;
    }

    // 向量内容检索器：匹配向量库中的数据
    @Bean
    public ContentRetriever contentRetriever() {
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.75)
                .build();

        log.info("向量内容检索器创建完成，使用模型：{}, 存储类型：{}",
                embeddingModel.getClass().getSimpleName(),
                embeddingStore.getClass().getSimpleName());

        return contentRetriever;
    }
}
