package com.example.hospital.models;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int userId;
    private String message;
    private Integer relatedRequestId;
    private LocalDateTime createdAt;
    private boolean isRead;

    public Notification() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getRelatedRequestId() {
        return relatedRequestId;
    }

    public void setRelatedRequestId(Integer relatedRequestId) {
        this.relatedRequestId = relatedRequestId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
