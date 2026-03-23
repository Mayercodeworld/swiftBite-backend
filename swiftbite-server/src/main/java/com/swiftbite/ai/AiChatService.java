package com.swiftbite.ai;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AiChatService {

//    // 普通调用
//    @SystemMessage(fromResource = "template/system-prompt.txt")
//    String chatStream(@MemoryId int memoryId, @UserMessage String userMessage); // 当多个用户访问时使用memoryId来区分


    // 流式输出
    @SystemMessage(fromResource = "template/system-prompt.txt")
    Flux<String> chatStream(@MemoryId int memoryId, @UserMessage String userMessage); // 当多个用户访问时使用memoryId来区分
}


