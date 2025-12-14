package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AppUser sender;

    @ManyToOne
    private AppUser target; // <-- новый (nullable)

    @ManyToOne
    private Chat chat;

    @Column(columnDefinition = "text")
    private String text;

    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean delivered = false;
    private boolean read = false;

    public Message() {}
    public Message(AppUser sender, Chat chat, String text) {
        this.sender = sender;
        this.chat = chat;
        this.text = text;
        this.createdAt = LocalDateTime.now();
    }

    public Message(AppUser sender, AppUser target, Chat chat, String text) {
        this.sender = sender;
        this.target = target;
        this.chat = chat;
        this.text = text;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public AppUser getSender() { return sender; }
    public AppUser getTarget() { return target; }
    public void setTarget(AppUser target) { this.target = target; }
    public Chat getChat() { return chat; }
    public String getText() { return text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isDelivered() { return delivered; }
    public void setDelivered(boolean delivered) { this.delivered = delivered; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
