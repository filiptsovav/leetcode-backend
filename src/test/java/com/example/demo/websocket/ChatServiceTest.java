package com.example.demo.websocket;

import com.example.demo.model.AppUser;
import com.example.demo.model.Chat;
import com.example.demo.model.Message;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    // @Test
    // void sendPrivateMessage_success() {
    //     AppUser sender = new AppUser("alice","pass");
    //     sender = setId(sender, 1L);
    //     AppUser recipient = new AppUser("bob","pass");
    //     recipient = setId(recipient, 2L);

    //     when(userRepository.findByUsername("alice")).thenReturn(sender);
    //     when(userRepository.findByUsername("bob")).thenReturn(recipient);

    //     Chat savedChat = new Chat();
    //     savedChat.getUsers().add(sender);
    //     savedChat.getUsers().add(recipient);
    //     // emulate id set after save
    //     setField(savedChat, "id", 100L);

    //     when(chatRepository.save(any(Chat.class))).thenReturn(savedChat);

    //     Message toSave = new Message(sender, savedChat, "hi bob");
    //     setField(toSave, "id", 200L);
    //     when(messageRepository.save(any(Message.class))).thenAnswer(inv -> {
    //         Message m = inv.getArgument(0);
    //         setField(m, "id", 200L);
    //         return m;
    //     });

    //     Message res;
    //     try {
    //         res = ChatService.sendPrivateMessage("alice","bob","hi bob");
    //         assertNotNull(res);
    //         assertEquals(200L, res.getId());
    //         assertEquals("hi bob", res.getText());
    //         assertEquals("alice", res.getSender().getUsername());
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     verify(userRepository, times(1)).findByUsername("alice");
    //     verify(userRepository, times(1)).findByUsername("bob");
    //     verify(chatRepository, times(1)).save(any(Chat.class));
    //     verify(messageRepository, times(1)).save(any(Message.class));
    // }

    // helpers to set private fields (id)
    private static AppUser setId(AppUser u, Long id) {
        try {
            java.lang.reflect.Field f = AppUser.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(u, id);
            return u;
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private static void setField(Object obj, String name, Object value) {
        try {
            java.lang.reflect.Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
