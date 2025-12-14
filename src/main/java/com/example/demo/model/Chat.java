package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Для публичного чата можно хранить флаг isPublic
    private boolean isPublic = false;

    private boolean isAnnouncement = false;

    @ManyToMany
    @JoinTable(
        name = "chat_users",
        joinColumns = @JoinColumn(name = "chat_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<AppUser> users = new ArrayList<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    // getters / setters
    public Long getId() { return id; }
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }
    public List<AppUser> getUsers() { return users; }
    public List<Message> getMessages() { return messages; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isAnnouncement() { return isAnnouncement; }
    public void setAnnouncement(boolean announcement) { isAnnouncement = announcement; }
}


