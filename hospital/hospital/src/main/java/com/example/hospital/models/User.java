package com.example.hospital.models;

public class User {
    private int id;
    private String username;
    private String fullname;
    private String role;

    public User(int id, String username, String fullname, String role) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getRole() {
        return role;
    }
}
