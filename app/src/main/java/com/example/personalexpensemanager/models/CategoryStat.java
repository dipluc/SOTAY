package com.example.personalexpensemanager.models;

public class CategoryStat {
    private long categoryId;
    private String categoryName;
    private String iconName;
    private long totalAmount;
    private float percentage;
    private String type;

    public CategoryStat(long categoryId, String categoryName, String iconName, long totalAmount, float percentage, String type) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.iconName = iconName;
        this.totalAmount = totalAmount;
        this.percentage = percentage;
        this.type = type;
    }

    public long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getIconName() { return iconName; }
    public long getTotalAmount() { return totalAmount; }
    public float getPercentage() { return percentage; }
    public void setPercentage(float percentage) { this.percentage = percentage; }
    public String getType() { return type; }
}