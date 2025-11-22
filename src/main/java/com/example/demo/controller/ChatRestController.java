package com.example.demo.controller;

import com.example.demo.dto.MessageDto;
import com.example.demo.model.Message;
import com.example.demo.model.Chat;
import com.example.demo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
public class ChatRestController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/create")
    public ResponseEntity<?> createChat(@RequestParam String username) {
        try {
            Chat chat = chatService.createChatWithUser(username);
            return ResponseEntity.status(201).body(chat);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{chatId}/messages")
    public List<MessageDto> getMessages(@PathVariable Long chatId) {
        List<Message> msgs = chatService.getMessages(chatId);
        return msgs.stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{chatId}/messages/since")
    public List<MessageDto> getMessagesSince(@PathVariable Long chatId, @RequestParam(required = false) Long lastId) {
        List<Message> msgs = chatService.getMessagesSince(chatId, lastId);
        return msgs.stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping("/{chatId}/messages")
    public MessageDto sendMessageRest(@PathVariable Long chatId, @RequestBody Map<String, String> body, @RequestHeader("Authorization") String authHeader) {
        // draw username from security context (or parse token)
        // for simplicity: assume jwtAuthenticationFilter populated SecurityContext for HTTP requests as usual
        String text = body.get("text");
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Message saved = chatService.sendMessageToChat(chatId, username, text);
        return toDto(saved);
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
