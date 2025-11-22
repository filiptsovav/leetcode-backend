package com.example.demo.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, org.springframework.web.socket.WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // Мы ожидаем, что JwtHandshakeInterceptor положил объект "principal" (UsernamePasswordAuthenticationToken) в attributes
        Object principal = attributes.get("principal");
        if (principal instanceof Principal) {
            return (Principal) principal;
        }
        return super.determineUser(request, wsHandler, attributes);
    }
}
