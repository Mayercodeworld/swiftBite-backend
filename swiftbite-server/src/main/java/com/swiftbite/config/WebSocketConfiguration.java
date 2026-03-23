package com.swiftbite.config;
// config下配置
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类：启用 Spring Boot 对 WebSocket 的原生支持
 */
@Configuration
public class WebSocketConfiguration {

    // 定义一个 ServerEndpointExporter Bean，让 Spring Boot 能够扫描并注册所有使用 @ServerEndpoint 注解的 WebSocket 端点
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
