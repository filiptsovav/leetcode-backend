package com.example.demo.websocket;

import com.example.demo.dto.MessageDto;
import com.example.demo.model.Message;
import com.example.demo.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    // Существующий публичный метод (оставляем)
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDto message, Authentication authentication) {
        String username = authentication.getName();
        Long chatId = message.getChatId() == null ? chatService.getOrCreatePublicChat().getId() : message.getChatId();
        Message saved = chatService.sendMessageToChat(chatId, username, message.getText());

        MessageDto out = toDto(saved);
        messagingTemplate.convertAndSend("/topic/public", out);
    }

    private MessageDto toDto(Message m) {
        MessageDto d = new MessageDto();
        d.setId(m.getId());
        d.setChatId(m.getChat().getId());
        d.setSenderId(m.getSender().getId());
        d.setSenderUsername(m.getSender().getUsername());
        d.setText(m.getText());
        d.setCreatedAt(m.getCreatedAt());
        return d;
    }
}
