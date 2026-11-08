package com.example.personalexpensemanager.models;

public class Category {
    public static final String TYPE_INCOME = "INCOME";
    public static final String TYPE_EXPENSE = "EXPENSE";

    private long id;
    private long userId;
    private String name;
    private String type; // INCOME / EXPENSE
    private String iconName;

    public Category() {}

    public Category(long id, long userId, String name, String type, String iconName) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.iconName = iconName;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
}