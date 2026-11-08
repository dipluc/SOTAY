package com.example.personalexpensemanager.models;

public class User {
    private long id;
    private String username;
    private String password;
    private String fullName;
    private String createdAt;

    public User() {}

    public User(long id, String username, String password, String fullName, String createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}