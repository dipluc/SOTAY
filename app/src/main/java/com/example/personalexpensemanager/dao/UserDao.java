package com.example.personalexpensemanager.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.personalexpensemanager.database.DatabaseHelper;
import com.example.personalexpensemanager.models.User;
import com.example.personalexpensemanager.utils.DateUtils;
import com.example.personalexpensemanager.utils.PasswordUtils;

public class UserDao {

    private DatabaseHelper dbHelper;

    public UserDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, new String[]{DatabaseHelper.COL_ID},
                DatabaseHelper.COL_USER_NAME + "=?", new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public long registerUser(String username, String password, String fullName) {
        if (checkUsernameExists(username)) return -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_USER_NAME, username);
        cv.put(DatabaseHelper.COL_PASSWORD, PasswordUtils.hashPassword(password));
        cv.put(DatabaseHelper.COL_FULL_NAME, fullName);
        cv.put(DatabaseHelper.COL_CREATED_AT, DateUtils.getTodayDbDate());
        long userId = db.insert(DatabaseHelper.TABLE_USERS, null, cv);
        if (userId != -1) {
            CategoryDao categoryDao = new CategoryDao(dbHelper);
            categoryDao.addDefaultCategoriesForUser(userId);
        }
        return userId;
    }

    public User loginUser(String username, String rawPassword) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String hashedPassword = PasswordUtils.hashPassword(rawPassword);
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null,
                DatabaseHelper.COL_USER_NAME + "=? AND " + DatabaseHelper.COL_PASSWORD + "=?",
                new String[]{username, hashedPassword}, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PASSWORD)));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FULL_NAME)));
            user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CREATED_AT)));
        }
        cursor.close();
        return user;
    }

    public boolean changePassword(long userId, String oldPassword, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String oldHash = PasswordUtils.hashPassword(oldPassword);
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, new String[]{DatabaseHelper.COL_ID},
                DatabaseHelper.COL_ID + "=? AND " + DatabaseHelper.COL_PASSWORD + "=?",
                new String[]{String.valueOf(userId), oldHash}, null, null, null);

        boolean validOld = cursor.getCount() > 0;
        cursor.close();

        if (!validOld) return false;

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_PASSWORD, PasswordUtils.hashPassword(newPassword));
        int rows = db.update(DatabaseHelper.TABLE_USERS, cv, DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }
}