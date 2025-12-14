package com.example.demo.service;

import com.example.demo.dto.MessageDto;
import com.example.demo.model.AppUser;
import com.example.demo.model.Message;
import com.example.demo.model.Chat;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyMessage(Message m) {
        MessageDto dto = toDto(m);
        Chat chat = m.getChat();

        // Topic per chat (useful for chat-specific subscriptions)
        if (chat != null) {
            messagingTemplate.convertAndSend("/topic/chats/" + chat.getId(), dto);
        }

        // If public chat, duplicate to public topic (backwards compatibility)
        if (chat != null && chat.isPublic()) {
            messagingTemplate.convertAndSend("/topic/public", dto);
        }

        // Per-user queues (useful for mobile/push clients subscribed to /user/{username}/queue/messages)
        List<AppUser> users = chat != null ? chat.getUsers() : List.of();
        for (AppUser u : users) {
            if (u != null && u.getUsername() != null) {
                messagingTemplate.convertAndSendToUser(u.getUsername(), "/queue/messages", dto);
            }
        }
    }

    private MessageDto toDto(Message m) {
        MessageDto d = new MessageDto();
        d.setId(m.getId());
        d.setChatId(m.getChat() != null ? m.getChat().getId() : null);
        d.setSenderId(m.getSender() != null ? m.getSender().getId() : null);
        d.setSenderUsername(m.getSender() != null ? m.getSender().getUsername() : null);
        d.setTargetUsername(m.getTarget() != null ? m.getTarget().getUsername() : null);
        d.setText(m.getText());
        d.setCreatedAt(m.getCreatedAt());
        return d;
    }
}
