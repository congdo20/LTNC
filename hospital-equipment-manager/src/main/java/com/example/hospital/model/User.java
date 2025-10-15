package com.example.hospital.model;

public class User {
    private int id;
    private String username;
    private String fullname;
    private String role; // "ADMIN" or "USER"

    public User() {
    }

    public User(int id, String username, String fullname) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
    }

    public User(int id, String username, String fullname, String role) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}