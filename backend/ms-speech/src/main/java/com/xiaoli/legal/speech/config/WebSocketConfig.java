package com.xiaoli.legal.speech.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocket配置
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Value("${websocket.allowed-origins:*}")
    private String allowedOrigins;
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单内存消息代理
        config.enableSimpleBroker("/topic", "/queue");
        // 客户端发送消息到服务器的前缀
        config.setApplicationDestinationPrefixes("/app");
        // 服务器推送消息给特定用户的前缀
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册STOMP端点
        registry.addEndpoint("/ws/speech")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        // WebSocket端点(不带SockJS)
        registry.addEndpoint("/ws/speech")
                .setAllowedOriginPatterns("*");
    }
}
