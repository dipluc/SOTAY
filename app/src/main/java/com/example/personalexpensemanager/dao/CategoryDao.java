package com.example.personalexpensemanager.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.personalexpensemanager.database.DatabaseHelper;
import com.example.personalexpensemanager.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDao {

    private DatabaseHelper dbHelper;

    public CategoryDao(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public CategoryDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void addDefaultCategoriesForUser(long userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Income
        insertCatInternal(db, userId, "Lương", Category.TYPE_INCOME, "ic_salary");
        insertCatInternal(db, userId, "Thưởng", Category.TYPE_INCOME, "ic_bonus");
        insertCatInternal(db, userId, "Học bổng", Category.TYPE_INCOME, "ic_scholarship");
        insertCatInternal(db, userId, "Thu nhập khác", Category.TYPE_INCOME, "ic_other_income");

        // Expense
        insertCatInternal(db, userId, "Ăn uống", Category.TYPE_EXPENSE, "ic_food");
        insertCatInternal(db, userId, "Di chuyển", Category.TYPE_EXPENSE, "ic_transport");
        insertCatInternal(db, userId, "Mua sắm", Category.TYPE_EXPENSE, "ic_shopping");
        insertCatInternal(db, userId, "Giải trí", Category.TYPE_EXPENSE, "ic_entertainment");
        insertCatInternal(db, userId, "Học tập", Category.TYPE_EXPENSE, "ic_study");
        insertCatInternal(db, userId, "Khác", Category.TYPE_EXPENSE, "ic_other");
    }

    private void insertCatInternal(SQLiteDatabase db, long userId, String name, String type, String icon) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_CAT_USER_ID, userId);
        cv.put(DatabaseHelper.COL_CAT_NAME, name);
        cv.put(DatabaseHelper.COL_CAT_TYPE, type);
        cv.put(DatabaseHelper.COL_CAT_ICON, icon);
        db.insert(DatabaseHelper.TABLE_CATEGORIES, null, cv);
    }

    public long insertCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_CAT_USER_ID, category.getUserId());
        cv.put(DatabaseHelper.COL_CAT_NAME, category.getName());
        cv.put(DatabaseHelper.COL_CAT_TYPE, category.getType());
        cv.put(DatabaseHelper.COL_CAT_ICON, category.getIconName());
        return db.insert(DatabaseHelper.TABLE_CATEGORIES, null, cv);
    }

    public boolean updateCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_CAT_NAME, category.getName());
        cv.put(DatabaseHelper.COL_CAT_ICON, category.getIconName());
        int rows = db.update(DatabaseHelper.TABLE_CATEGORIES, cv, DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(category.getId())});
        return rows > 0;
    }

    public boolean deleteCategory(long categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_CATEGORIES, DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(categoryId)});
        return rows > 0;
    }

    public List<Category> getCategoriesByUser(long userId, String typeFilter) {
        List<Category> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COL_CAT_USER_ID + "=?";
        List<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(String.valueOf(userId));

        if (typeFilter != null && !typeFilter.isEmpty() && !typeFilter.equalsIgnoreCase("ALL")) {
            selection += " AND " + DatabaseHelper.COL_CAT_TYPE + "=?";
            selectionArgs.add(typeFilter);
        }

        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORIES, null, selection,
                selectionArgs.toArray(new String[0]), null, null, DatabaseHelper.COL_CAT_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Category cat = new Category();
                cat.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
                cat.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_USER_ID)));
                cat.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_NAME)));
                cat.setType(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_TYPE)));
                cat.setIconName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_ICON)));
                list.add(cat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}