package com.example.interestcommunity.models;

public class Post {
    private String id;
    private String title;
    private String content;
    private String userId;
    private String authorName;
    private String category; // Новое поле для темы
    private long timestamp;

    public Post() {}

    public Post(String title, String content, String userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}