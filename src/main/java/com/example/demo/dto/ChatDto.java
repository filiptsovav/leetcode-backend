package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ChatDto {
    private Long id;
    private boolean isPublic;
    private List<String> users;
    private LocalDateTime createdAt;

    // Геттеры
    public Long getId() {
        return id;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public List<String> getUsers() {
        return users;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Сеттеры
    public void setId(Long id) {
        this.id = id;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}