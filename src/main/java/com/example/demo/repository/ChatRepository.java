package com.example.demo.repository;

import com.example.demo.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    Optional<Chat> findByIsPublicTrue();
    Optional<Chat> findByIsAnnouncementTrue();

    // Find 1-on-1 chat specifically between two users
    @Query("SELECT c FROM Chat c JOIN c.users u1 JOIN c.users u2 " +
           "WHERE u1.id = :id1 AND u2.id = :id2")
    Optional<Chat> findChatBetween(@Param("id1") Long id1, @Param("id2") Long id2);

    // NEW: Find all chats a specific user belongs to
    @Query("SELECT DISTINCT c FROM Chat c JOIN c.users u WHERE u.username = :username")
    List<Chat> findAllByUsername(@Param("username") String username);
}