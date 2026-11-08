package com.example.personalexpensemanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String PREF_NAME = "PersonalExpensePrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_REMEMBERED_USERNAME = "remembered_username";

    private SharedPreferences sharedPreferences;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(long userId, String username, String fullName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getUserId() != -1;
    }

    public long getUserId() {
        return sharedPreferences.getLong(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public String getFullName() {
        return sharedPreferences.getString(KEY_FULL_NAME, "Người dùng");
    }

    public void setDarkMode(boolean isDarkMode) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isDarkMode).apply();
    }

    public boolean isDarkMode() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    public void setRememberedUsername(String username) {
        sharedPreferences.edit().putString(KEY_REMEMBERED_USERNAME, username).apply();
    }

    public String getRememberedUsername() {
        return sharedPreferences.getString(KEY_REMEMBERED_USERNAME, "");
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_FULL_NAME);
        editor.apply();
    }
}
