package com.example.personalexpensemanager.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.personalexpensemanager.database.DatabaseHelper;
import com.example.personalexpensemanager.models.CategoryStat;
import com.example.personalexpensemanager.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionDao {

    private DatabaseHelper dbHelper;

    public TransactionDao(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long insertTransaction(Transaction transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_TRANS_USER_ID, transaction.getUserId());
        cv.put(DatabaseHelper.COL_TRANS_CAT_ID, transaction.getCategoryId());
        cv.put(DatabaseHelper.COL_TRANS_AMOUNT, transaction.getAmount());
        cv.put(DatabaseHelper.COL_TRANS_NOTE, transaction.getNote());
        cv.put(DatabaseHelper.COL_TRANS_DATE, transaction.getDate());
        cv.put(DatabaseHelper.COL_TRANS_IMAGE_URI, transaction.getImageUri());
        return db.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, cv);
    }

    public boolean updateTransaction(Transaction transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_TRANS_CAT_ID, transaction.getCategoryId());
        cv.put(DatabaseHelper.COL_TRANS_AMOUNT, transaction.getAmount());
        cv.put(DatabaseHelper.COL_TRANS_NOTE, transaction.getNote());
        cv.put(DatabaseHelper.COL_TRANS_DATE, transaction.getDate());
        cv.put(DatabaseHelper.COL_TRANS_IMAGE_URI, transaction.getImageUri());
        int rows = db.update(DatabaseHelper.TABLE_TRANSACTIONS, cv, DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(transaction.getId())});
        return rows > 0;
    }

    public boolean deleteTransaction(long transactionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_TRANSACTIONS, DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(transactionId)});
        return rows > 0;
    }

    public int deleteTransactionsByUser(long userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_TRANSACTIONS,
                DatabaseHelper.COL_TRANS_USER_ID + "=?", new String[]{String.valueOf(userId)});
    }

    public List<Transaction> getTransactionsByUser(long userId, String typeFilter, String monthYear) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT t.*, c." + DatabaseHelper.COL_CAT_NAME + " AS cat_name, c."
                + DatabaseHelper.COL_CAT_ICON + " AS cat_icon, c."
                + DatabaseHelper.COL_CAT_TYPE + " AS cat_type FROM "
                + DatabaseHelper.TABLE_TRANSACTIONS + " t JOIN "
                + DatabaseHelper.TABLE_CATEGORIES + " c ON t."
                + DatabaseHelper.COL_TRANS_CAT_ID + " = c." + DatabaseHelper.COL_ID
                + " WHERE t." + DatabaseHelper.COL_TRANS_USER_ID + " = ?";

        List<String> args = new ArrayList<>();
        args.add(String.valueOf(userId));

        if (typeFilter != null && !typeFilter.isEmpty() && !typeFilter.equalsIgnoreCase("ALL")) {
            query += " AND c." + DatabaseHelper.COL_CAT_TYPE + " = ?";
            args.add(typeFilter);
        }

        if (monthYear != null && !monthYear.isEmpty()) {
            query += " AND t." + DatabaseHelper.COL_TRANS_DATE + " LIKE ?";
            args.add(monthYear + "%");
        }

        query += " ORDER BY t." + DatabaseHelper.COL_TRANS_DATE + " DESC, t." + DatabaseHelper.COL_ID + " DESC";

        Cursor cursor = db.rawQuery(query, args.toArray(new String[0]));
        if (cursor.moveToFirst()) {
            do {
                Transaction t = new Transaction();
                t.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
                t.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_USER_ID)));
                t.setCategoryId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_CAT_ID)));
                t.setAmount(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_AMOUNT)));
                t.setNote(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_NOTE)));
                t.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_DATE)));
                t.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_IMAGE_URI)));
                t.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow("cat_name")));
                t.setCategoryIcon(cursor.getString(cursor.getColumnIndexOrThrow("cat_icon")));
                t.setCategoryType(cursor.getString(cursor.getColumnIndexOrThrow("cat_type")));
                list.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Transaction> searchTransactions(long userId, String keyword) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT t.*, c." + DatabaseHelper.COL_CAT_NAME + " AS cat_name, c."
                + DatabaseHelper.COL_CAT_ICON + " AS cat_icon, c."
                + DatabaseHelper.COL_CAT_TYPE + " AS cat_type FROM "
                + DatabaseHelper.TABLE_TRANSACTIONS + " t JOIN "
                + DatabaseHelper.TABLE_CATEGORIES + " c ON t."
                + DatabaseHelper.COL_TRANS_CAT_ID + " = c." + DatabaseHelper.COL_ID
                + " WHERE t." + DatabaseHelper.COL_TRANS_USER_ID + " = ?"
                + " AND (t." + DatabaseHelper.COL_TRANS_NOTE + " LIKE ? OR c." + DatabaseHelper.COL_CAT_NAME + " LIKE ?)"
                + " ORDER BY t." + DatabaseHelper.COL_TRANS_DATE + " DESC, t." + DatabaseHelper.COL_ID + " DESC";

        String pattern = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), pattern, pattern});
        if (cursor.moveToFirst()) {
            do {
                Transaction t = new Transaction();
                t.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
                t.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_USER_ID)));
                t.setCategoryId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_CAT_ID)));
                t.setAmount(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_AMOUNT)));
                t.setNote(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_NOTE)));
                t.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_DATE)));
                t.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_IMAGE_URI)));
                t.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow("cat_name")));
                t.setCategoryIcon(cursor.getString(cursor.getColumnIndexOrThrow("cat_icon")));
                t.setCategoryType(cursor.getString(cursor.getColumnIndexOrThrow("cat_type")));
                list.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public long getTotalAmountByType(long userId, String type, String monthYear) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT SUM(t." + DatabaseHelper.COL_TRANS_AMOUNT + ") FROM "
                + DatabaseHelper.TABLE_TRANSACTIONS + " t JOIN "
                + DatabaseHelper.TABLE_CATEGORIES + " c ON t."
                + DatabaseHelper.COL_TRANS_CAT_ID + " = c." + DatabaseHelper.COL_ID
                + " WHERE t." + DatabaseHelper.COL_TRANS_USER_ID + " = ?"
                + " AND c." + DatabaseHelper.COL_CAT_TYPE + " = ?";

        List<String> args = new ArrayList<>();
        args.add(String.valueOf(userId));
        args.add(type);

        if (monthYear != null && !monthYear.isEmpty()) {
            query += " AND t." + DatabaseHelper.COL_TRANS_DATE + " LIKE ?";
            args.add(monthYear + "%");
        }

        Cursor cursor = db.rawQuery(query, args.toArray(new String[0]));
        long total = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }

    public List<CategoryStat> getCategoryStats(long userId, String type, String monthYear) {
        List<CategoryStat> list = new ArrayList<>();
        long grandTotal = getTotalAmountByType(userId, type, monthYear);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT c." + DatabaseHelper.COL_ID + " AS cat_id, c."
                + DatabaseHelper.COL_CAT_NAME + " AS cat_name, c."
                + DatabaseHelper.COL_CAT_ICON + " AS cat_icon, SUM(t."
                + DatabaseHelper.COL_TRANS_AMOUNT + ") AS cat_total FROM "
                + DatabaseHelper.TABLE_TRANSACTIONS + " t JOIN "
                + DatabaseHelper.TABLE_CATEGORIES + " c ON t."
                + DatabaseHelper.COL_TRANS_CAT_ID + " = c." + DatabaseHelper.COL_ID
                + " WHERE t." + DatabaseHelper.COL_TRANS_USER_ID + " = ?"
                + " AND c." + DatabaseHelper.COL_CAT_TYPE + " = ?";

        List<String> args = new ArrayList<>();
        args.add(String.valueOf(userId));
        args.add(type);

        if (monthYear != null && !monthYear.isEmpty()) {
            query += " AND t." + DatabaseHelper.COL_TRANS_DATE + " LIKE ?";
            args.add(monthYear + "%");
        }

        query += " GROUP BY c." + DatabaseHelper.COL_ID + " ORDER BY cat_total DESC";

        Cursor cursor = db.rawQuery(query, args.toArray(new String[0]));
        if (cursor.moveToFirst()) {
            do {
                long catId = cursor.getLong(cursor.getColumnIndexOrThrow("cat_id"));
                String catName = cursor.getString(cursor.getColumnIndexOrThrow("cat_name"));
                String catIcon = cursor.getString(cursor.getColumnIndexOrThrow("cat_icon"));
                long total = cursor.getLong(cursor.getColumnIndexOrThrow("cat_total"));
                float pct = grandTotal > 0 ? ((float) total / grandTotal) * 100f : 0f;

                list.add(new CategoryStat(catId, catName, catIcon, total, pct, type));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
