package com.swiftbite.config;

import com.swiftbite.ai.AiChatService;
import com.swiftbite.ai.AiTools;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 标记为配置类
public class AiChatConfiguration {

    // 向量内容检索器（返回向量存储中相似的信息）
    @Resource
    private ContentRetriever contentRetriever;

    //@Resource
    //private McpToolProvider mcpToolProvider;

    // 流式输出模型
    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    // 非流式输出模型
//    @Resource
//    private ChatModel qwenChatModel;

    @Resource
    private AiTools aiTools;

    @Bean // 自动创建AiCodeHelperService接口的实现类
    public AiChatService aiCodeHelperService() {
        // 会话记忆，支持每个用户最多保存10条历史会话，且是基于内存的
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        // 构造AI Service
        AiChatService aiChatService = AiServices.builder(AiChatService.class)
                .streamingChatModel(qwenStreamingChatModel) // 流式输入
                //.chatModel(qwenChatModel)
                .chatMemory(chatMemory) // 会话记忆
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10)) // 每个会话独立存储
                .contentRetriever(contentRetriever) // RAG检索增强生成
                .tools(aiTools) // 添加工具
                // .toolProvider(mcpToolProvider) // MCP工具调用
                .build();
        return aiChatService;
    }
}

