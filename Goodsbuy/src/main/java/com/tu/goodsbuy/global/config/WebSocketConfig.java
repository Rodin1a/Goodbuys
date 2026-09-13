package com.tu.goodsbuy.global.config;

import com.tu.goodsbuy.model.dto.MemberUser;
import com.tu.goodsbuy.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ChatService chatService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor headers = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (headers == null) throw new IllegalArgumentException("Missing STOMP headers");
                var command = headers.getCommand();
                if (command != StompCommand.CONNECT && command != StompCommand.SEND
                        && command != StompCommand.SUBSCRIBE) return message;
                var attributes = headers.getSessionAttributes();
                MemberUser member = attributes == null ? null : (MemberUser) attributes.get("loginMember");
                if (member == null) throw new IllegalArgumentException("Login required");
                if (command != StompCommand.CONNECT) {
                    String destination = headers.getDestination();
                    String pattern = command == StompCommand.SUBSCRIBE
                            ? "^/sub/(messages|chat)/[0-9]+$" : "^/pub/(enter|chat)/[0-9]+$";
                    if (destination == null || !destination.matches(pattern)) {
                        throw new IllegalArgumentException("Invalid chat destination");
                    }
                    Long roomNo = Long.valueOf(destination.substring(destination.lastIndexOf('/') + 1));
                    chatService.getRecipientIdBySenderNo(roomNo, member.getUserNo());
                }
                return message;
            }
        });
    }
}
