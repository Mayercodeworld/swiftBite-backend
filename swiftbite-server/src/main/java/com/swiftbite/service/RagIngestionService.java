package com.swiftbite.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;

import java.util.List;

/**
 * RAG 数据摄入接口
 */
public interface RagIngestionService {
    /**
     * 摄入文档列表
     * 适用于：本地文件加载、PDF解析结果、Redis、数据库查询拼装后的结果等原始 Document 对象
     * @param documents 待处理的文档列表
     */
    public void ingestDocuments(List<Document> documents);

    /**
     * (可选) 清空当前集合
     * 用于全量重建索引前的清理工作
     */
    public void clearStore();
}
