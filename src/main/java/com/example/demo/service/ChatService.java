package com.example.demo.service;

import com.example.demo.model.Chat;
import com.example.demo.model.Message;
import com.example.demo.model.AppUser;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public Message sendMessageToChat(Long chatId, String username, String text) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        AppUser sender = userRepository.findByUsername(username);
        if (sender == null) throw new RuntimeException("User not found");
        Message m = new Message(sender, chat, text);
        m = messageRepository.save(m);
        return m;
    }

    public Chat createChatWithUser(String username) {
        AppUser user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("User not found");
        Chat chat = new Chat();
        chat.getUsers().add(user);
        return chatRepository.save(chat);
    }
}
