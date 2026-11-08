package com.example.personalexpensemanager.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.personalexpensemanager.database.DatabaseHelper;
import com.example.personalexpensemanager.models.Budget;

public class BudgetDao {

    private DatabaseHelper dbHelper;

    public BudgetDao(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long setBudget(long userId, String monthYear, long limitAmount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if exists
        Cursor cursor = db.query(DatabaseHelper.TABLE_BUDGETS, new String[]{DatabaseHelper.COL_ID},
                DatabaseHelper.COL_BUDGET_USER_ID + "=? AND " + DatabaseHelper.COL_BUDGET_MONTH + "=?",
                new String[]{String.valueOf(userId), monthYear}, null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_BUDGET_USER_ID, userId);
        cv.put(DatabaseHelper.COL_BUDGET_MONTH, monthYear);
        cv.put(DatabaseHelper.COL_BUDGET_LIMIT, limitAmount);

        if (exists) {
            db.update(DatabaseHelper.TABLE_BUDGETS, cv,
                    DatabaseHelper.COL_BUDGET_USER_ID + "=? AND " + DatabaseHelper.COL_BUDGET_MONTH + "=?",
                    new String[]{String.valueOf(userId), monthYear});
            return 1;
        } else {
            return db.insert(DatabaseHelper.TABLE_BUDGETS, null, cv);
        }
    }

    public Budget getBudget(long userId, String monthYear) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_BUDGETS, null,
                DatabaseHelper.COL_BUDGET_USER_ID + "=? AND " + DatabaseHelper.COL_BUDGET_MONTH + "=?",
                new String[]{String.valueOf(userId), monthYear}, null, null, null);

        Budget budget = null;
        if (cursor.moveToFirst()) {
            budget = new Budget();
            budget.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
            budget.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BUDGET_USER_ID)));
            budget.setMonthYear(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BUDGET_MONTH)));
            budget.setLimitAmount(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BUDGET_LIMIT)));
        }
        cursor.close();
        return budget;
    }
}