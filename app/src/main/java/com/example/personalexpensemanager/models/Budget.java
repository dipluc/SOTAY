package com.example.personalexpensemanager.models;

public class Budget {
    private long id;
    private long userId;
    private String monthYear; // YYYY-MM
    private long limitAmount;

    public Budget() {}

    public Budget(long id, long userId, String monthYear, long limitAmount) {
        this.id = id;
        this.userId = userId;
        this.monthYear = monthYear;
        this.limitAmount = limitAmount;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }

    public long getLimitAmount() { return limitAmount; }
    public void setLimitAmount(long limitAmount) { this.limitAmount = limitAmount; }
}