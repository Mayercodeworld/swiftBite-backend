package com.sky.config;
// config下配置
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类，用于注册WebSocket的Bean
 */
@Configuration
public class WebSocketConfiguration {

    // 该Bean在初始化时，扫描整个应用上下文中所有@ServerEndpoint类，将其注册为WebSocket端点
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    } //启用 JSR-356 WebSocket 支持的关键 Bean

}
