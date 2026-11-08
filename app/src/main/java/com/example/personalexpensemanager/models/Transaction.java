package com.example.personalexpensemanager.models;

public class Transaction {
    private long id;
    private long userId;
    private long categoryId;
    private long amount;
    private String note;
    private String date; // YYYY-MM-DD
    private String imageUri;

    // Joined Category Info
    private String categoryName;
    private String categoryIcon;
    private String categoryType;

    public Transaction() {}

    public Transaction(long id, long userId, long categoryId, long amount, String note, String date, String imageUri) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.imageUri = imageUri;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }

    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getCategoryIcon() { return categoryIcon; }
    public void setCategoryIcon(String categoryIcon) { this.categoryIcon = categoryIcon; }

    public String getCategoryType() { return categoryType; }
    public void setCategoryType(String categoryType) { this.categoryType = categoryType; }
}