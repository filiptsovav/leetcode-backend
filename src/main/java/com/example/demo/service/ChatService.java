package com.example.demo.service;

import com.example.demo.model.Chat;
import com.example.demo.model.Message;
import com.example.demo.model.AppUser;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    ChatService() {}

    public Chat getOrCreateAnnouncementChat() {
        Optional<Chat> opt = chatRepository.findByIsAnnouncementTrue();
        if (opt.isPresent()) return opt.get();
        
        Chat c = new Chat();
        c.setAnnouncement(true);
        return chatRepository.save(c);
    }

    public Chat getOrCreatePublicChat() {
        Optional<Chat> opt = chatRepository.findByIsPublicTrue();
        if (opt.isPresent()) return opt.get();
        Chat c = new Chat();
        c.setPublic(true);
        return chatRepository.save(c);
    }

    

    public List<Message> getMessages(Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        return messageRepository.findByChatOrderByCreatedAtAsc(chat);
    }

    public List<Message> getMessagesSince(Long chatId, Long lastId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        if (lastId == null) return messageRepository.findByChatOrderByCreatedAtAsc(chat);
        return messageRepository.findByChatAndIdGreaterThanOrderByCreatedAtAsc(chat, lastId);
    }

    // original — без target
    public Message sendMessageToChat(Long chatId, String username, String text) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        AppUser sender = userRepository.findByUsername(username);
        if (sender == null) throw new RuntimeException("User not found");
        Message m = new Message(sender, chat, text);
        m = messageRepository.save(m);
        return m;
    }

    // new overload: with targetUsername (nullable)
    public Message sendMessageToChat(Long chatId, String username, String text, String targetUsername) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        if (chat.isAnnouncement()) {
            if (!"123123".equals(username)) {
                throw new RuntimeException("Only admin (123123) can post in Advertisements!");
            }
        }
        AppUser sender = userRepository.findByUsername(username);
        if (sender == null) throw new RuntimeException("User not found");
        AppUser target = null;
        if (targetUsername != null) {
            target = userRepository.findByUsername(targetUsername);
            if (target == null) throw new RuntimeException("Target user not found");
        }
        Message m;
        if (target != null) {
            m = new Message(sender, target, chat, text);
        } else {
            m = new Message(sender, chat, text);
        }
        m = messageRepository.save(m);
        return m;
    }

    public Chat createChatWithUser(String currentUsername, String otherUsername) {
        AppUser current = userRepository.findByUsername(currentUsername);
        AppUser other = userRepository.findByUsername(otherUsername);
        if (current == null || other == null) throw new RuntimeException("User not found");

        // try find existing chat between two users
        Optional<Chat> existing = chatRepository.findChatBetween(current.getId(), other.getId());
        if (existing.isPresent()) return existing.get();

        Chat chat = new Chat();
        chat.getUsers().add(current);
        chat.getUsers().add(other);
        return chatRepository.save(chat);
    }

    public List<Chat> getChatsForUser(String username) {
        // 1. Приватные чаты
        List<Chat> userChats = chatRepository.findAllByUsername(username);
        
        List<Chat> result = new ArrayList<>();

        // 2. Сначала добавляем чат ОБЪЯВЛЕНИЙ (чтобы был самым верхним или вторым)
        Chat adsChat = getOrCreateAnnouncementChat();
        if (!userChats.contains(adsChat)) {
            result.add(adsChat);
        }

        // 3. Потом общий чат
        Chat publicChat = getOrCreatePublicChat();
        if (!userChats.contains(publicChat)) {
            result.add(publicChat);
        }
        
        // 4. Потом личные
        result.addAll(userChats);
        
        return result;
    }
}
