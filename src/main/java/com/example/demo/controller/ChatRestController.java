package com.example.demo.controller;

import com.example.demo.dto.ChatDto;
import com.example.demo.dto.MessageDto;
import com.example.demo.model.AppUser;
import com.example.demo.model.Chat;
import com.example.demo.model.Message;
import com.example.demo.service.ChatNotificationService;
import com.example.demo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
public class ChatRestController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatNotificationService notificationService;

    /**
     * Get all chats for the current user (includes Public chat).
     */
    @GetMapping
    public List<ChatDto> getMyChats() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Chat> chats = chatService.getChatsForUser(username);
        return chats.stream().map(this::toChatDto).collect(Collectors.toList());
    }

    /**
     * Get or create the specific Public Chat info.
     */
    @GetMapping("/public")
    public ChatDto getPublicChat() {
        Chat chat = chatService.getOrCreatePublicChat();
        return toChatDto(chat);
    }

    /**
     * Create or retrieve a 1-on-1 private chat.
     * Usage: POST /api/chats/create?username=otherUser
     */
    @PostMapping("/create")
    public ResponseEntity<?> createChat(@RequestParam String username) {
        try {
            String current = SecurityContextHolder.getContext().getAuthentication().getName();
            // Prevent chatting with self if desired
            if (current.equals(username)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cannot create chat with yourself"));
            }
            Chat chat = chatService.createChatWithUser(current, username);
            return ResponseEntity.status(201).body(toChatDto(chat));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Get message history for a specific chat.
     */
    @GetMapping("/{chatId}/messages")
    public List<MessageDto> getMessages(@PathVariable Long chatId) {
        List<Message> msgs = chatService.getMessages(chatId);
        return msgs.stream().map(this::toMessageDto).collect(Collectors.toList());
    }

    /**
     * Send a message via HTTP (fallback for WebSocket).
     */
    @PostMapping("/{chatId}/messages")
    public MessageDto sendMessageRest(@PathVariable Long chatId, 
                                      @RequestBody Map<String, String> body) {
        String text = body.get("text");
        String targetUsername = body.get("targetUsername"); // optional
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Message saved = chatService.sendMessageToChat(chatId, username, text, targetUsername);

        // Broadcast to WebSocket subscribers so they see the update immediately
        notificationService.notifyMessage(saved);

        return toMessageDto(saved);
    }

    private ChatDto toChatDto(Chat chat) {
        ChatDto dto = new ChatDto();
        dto.setId(chat.getId());
        dto.setIsPublic(chat.isPublic());
        dto.setIsAnnouncement(chat.isAnnouncement());
        dto.setCreatedAt(chat.getCreatedAt());
        // Map List<AppUser> to List<String> (usernames) to avoid recursion/passwords
        List<String> usernames = chat.getUsers().stream()
                .map(AppUser::getUsername)
                .collect(Collectors.toList());
        dto.setUsers(usernames);
        return dto;
    }

    private MessageDto toMessageDto(Message m) {
        MessageDto d = new MessageDto();
        d.setId(m.getId());
        d.setChatId(m.getChat().getId());
        d.setSenderId(m.getSender().getId());
        d.setSenderUsername(m.getSender().getUsername());
        d.setTargetUsername(m.getTarget() != null ? m.getTarget().getUsername() : null);
        d.setText(m.getText());
        d.setCreatedAt(m.getCreatedAt());
        return d;
    }
}    