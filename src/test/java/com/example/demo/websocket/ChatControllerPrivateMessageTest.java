// package com.example.demo.websocket;

// import com.example.demo.dto.MessageDto;
// import com.example.demo.model.AppUser;
// import com.example.demo.model.Chat;
// import com.example.demo.model.Message;
// import com.example.demo.service.ChatService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.*;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;

// import java.time.LocalDateTime;
// import static org.mockito.Mockito.*;

// class ChatControllerPrivateMessageTest {

//     @Mock
//     private SimpMessagingTemplate messagingTemplate;

//     @Mock
//     private ChatService chatService;

//     private ChatController chatController;

//     @BeforeEach
//     void init() {
//         MockitoAnnotations.openMocks(this);
//         chatController = new ChatController(messagingTemplate, chatService);
//     }

//     // @Test
//     // void sendPrivateMessage_sendsToUserQueue() {
//     //     // prepare DTO incoming
//     //     MessageDto dto = new MessageDto();
//     //     dto.setTargetUsername("bob");
//     //     dto.setText("hello bob");

//     //     // prepare authentication
//     //     Authentication auth = new UsernamePasswordAuthenticationToken("alice", null);

//     //     // prepare saved message
//     //     AppUser sender = new AppUser("alice","pwd");
//     //     setId(sender, 1L);
//     //     Chat chat = new Chat();
//     //     setField(chat, "id", 10L);
//     //     chat.getUsers().add(sender);
//     //     Message saved = new Message(sender, chat, "hello bob");
//     //     setField(saved, "id", 55L);
//     //     setField(saved, "createdAt", LocalDateTime.now());

//     //     // when(chatService.sendPrivateMessage("alice", "bob", "hello bob")).thenReturn(saved);

//     //     // // call
//     //     // chatController.sendPrivateMessage(dto, auth);

//     //     // verify send to user called
//     //     verify(messagingTemplate, times(1)).convertAndSendToUser(eq("bob"), eq("/queue/messages"), any());
//     // }

//     // helpers
//     private static void setField(Object obj, String name, Object value) {
//         try {
//             java.lang.reflect.Field f = obj.getClass().getDeclaredField(name);
//             f.setAccessible(true);
//             f.set(obj, value);
//         } catch (Exception e) { throw new RuntimeException(e); }
//     }

//     private static void setId(AppUser u, Long id) {
//         try {
//             java.lang.reflect.Field f = AppUser.class.getDeclaredField("id");
//             f.setAccessible(true);
//             f.set(u, id);
//         } catch (Exception e) { throw new RuntimeException(e); }
//     }
// }
