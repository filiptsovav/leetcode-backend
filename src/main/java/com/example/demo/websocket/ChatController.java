package com.example.demo.websocket;

import com.example.demo.dto.MessageDto;
import com.example.demo.model.Message;
import com.example.demo.service.ChatService;
import com.example.demo.service.ChatNotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final ChatNotificationService notificationService;

    public ChatController(ChatService chatService, ChatNotificationService notificationService) {
        this.chatService = chatService;
        this.notificationService = notificationService;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDto message, Authentication authentication) {
        String username = authentication.getName();
        Long chatId = message.getChatId() == null ? chatService.getOrCreatePublicChat().getId() : message.getChatId();
        // use overload with possible targetUsername
        Message saved = chatService.sendMessageToChat(chatId, username, message.getText(), message.getTargetUsername());

        // notify everyone via notification service (sends to topic/chat and per-user queues)
        notificationService.notifyMessage(saved);
    }
}
