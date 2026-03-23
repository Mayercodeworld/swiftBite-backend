package com.swiftbite.controller.user;
import com.swiftbite.ai.AiChatService;
import com.swiftbite.dto.ChatRequestDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
@RequestMapping("/user/ai")
public class AiChatController {

    @Resource
    private AiChatService aiChatService;

    /**
     * 流式输出
     * @param request
     * @return
     */
    @PostMapping("/chat")
    public Flux<ServerSentEvent<String>> chat(@RequestBody ChatRequestDTO request) {
        log.info("用户提问：{}", request.getMessage());
        return aiChatService.chatStream(request.getMemoryId(), request.getMessage())
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * 非流式输出
     * @param request
     * @return
     */
//    @PostMapping("/chat")
//    public String chat(@RequestBody ChatRequestDTO request) {
//        //return aiChatService.chatStream(request.getMemoryId(), request.getMessage());
//        return """
//                推荐您尝试【老坛酸菜鱼】，价格 56 元，酸香开胃、鱼肉鲜嫩；搭配【清炒小油菜】，价格 18 元，清爽解腻；再配上一瓶【北冰洋】，价格 4 元，怀旧风味十足。这组搭配荤素均衡、口味丰富，适合日常正餐。
//
//                [CARD:dishId=51,name=老坛酸菜鱼，price=56.00,image=https://sky-itcast.oss-cn-beijing.aliyuncs.com/4a9cefba-6a74-467e-9fde-6e687ea725d7.png,description=原料：汤，草鱼，酸菜]
//                [CARD:dishId=54,name=清炒小油菜，price=18.00,image=https://sky-itcast.oss-cn-beijing.aliyuncs.com/3613d38e-5614-41c2-90ed-ff175bf50716.png,description=原料：小油菜]
//                [CARD:dishId=47,name=北冰洋，price=4.00,image=https://sky-itcast.oss-cn-beijing.aliyuncs.com/4451d4be-89a2-4939-9c69-3a87151cb979.png,description=还是小时候的味道]
//                """;
//    }
}
