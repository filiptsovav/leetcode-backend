package com.example.demo.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class AuthenticatedChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, org.springframework.messaging.MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        // Когда CONNECT — пользователь уже установлен в accessor.getUser() (via handshake handler).
        // Но для safety — если нет user, попробуем взять из simpConnectHeaders
        Principal user = accessor.getUser();
        if (user == null) {
            Object possible = accessor.getSessionAttributes() != null ? accessor.getSessionAttributes().get("principal") : null;
            if (possible instanceof Principal) {
                accessor.setUser((Principal) possible);
            }
        }
        return message;
    }
}
