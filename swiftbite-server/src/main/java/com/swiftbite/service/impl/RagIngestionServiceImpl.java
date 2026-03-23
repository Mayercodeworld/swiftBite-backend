package com.swiftbite.service.impl;

import com.swiftbite.service.RagIngestionService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RagIngestionServiceImpl implements RagIngestionService {

    @Resource
    private EmbeddingStoreIngestor embeddingStoreIngestor;

    /**
     * 摄入文档列表
     * 适用于：本地文件加载、PDF解析结果等原始 Document 对象
     * @param documents 待处理的文档列表
     */
    @Transactional
    public void ingestDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }
        long start = System.currentTimeMillis();
        embeddingStoreIngestor.ingest(documents);
        long end = System.currentTimeMillis();

        log.info("{} 个文档处理完成，耗时：{} ms", documents.size(), (end - start));
    }

    public void clearStore() {
        try {
            log.info("向量库已清空（需根据具体实现补充代码）。");
        } catch (Exception e) {
            log.error("清空向量库失败：{}", e.getMessage());
            throw new RuntimeException("清空向量库失败", e);
        }
    }
}
