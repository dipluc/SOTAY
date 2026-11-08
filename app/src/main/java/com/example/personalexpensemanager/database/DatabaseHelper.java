package com.example.personalexpensemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.personalexpensemanager.models.Category;
import com.example.personalexpensemanager.utils.PasswordUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PersonalExpenseDB.db";
    private static final int DATABASE_VERSION = 2;

    // Tables
    public static final String TABLE_USERS = "Users";
    public static final String TABLE_CATEGORIES = "Categories";
    public static final String TABLE_TRANSACTIONS = "Transactions";
    public static final String TABLE_BUDGETS = "Budgets";

    // Common columns
    public static final String COL_ID = "id";

    // Users columns
    public static final String COL_USER_NAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_FULL_NAME = "full_name";
    public static final String COL_CREATED_AT = "created_at";

    // Categories columns
    public static final String COL_CAT_USER_ID = "user_id";
    public static final String COL_CAT_NAME = "name";
    public static final String COL_CAT_TYPE = "type";
    public static final String COL_CAT_ICON = "icon_name";

    // Transactions columns
    public static final String COL_TRANS_USER_ID = "user_id";
    public static final String COL_TRANS_CAT_ID = "category_id";
    public static final String COL_TRANS_AMOUNT = "amount";
    public static final String COL_TRANS_NOTE = "note";
    public static final String COL_TRANS_DATE = "date";
    public static final String COL_TRANS_IMAGE_URI = "image_uri";

    // Budgets columns
    public static final String COL_BUDGET_USER_ID = "user_id";
    public static final String COL_BUDGET_MONTH = "month_year";
    public static final String COL_BUDGET_LIMIT = "limit_amount";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_NAME + " TEXT UNIQUE NOT NULL, "
                + COL_PASSWORD + " TEXT NOT NULL, "
                + COL_FULL_NAME + " TEXT, "
                + COL_CREATED_AT + " TEXT" + ");";

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CAT_USER_ID + " INTEGER, "
                + COL_CAT_NAME + " TEXT NOT NULL, "
                + COL_CAT_TYPE + " TEXT NOT NULL, "
                + COL_CAT_ICON + " TEXT, "
                + "FOREIGN KEY(" + COL_CAT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + ") ON DELETE CASCADE);";

        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TRANS_USER_ID + " INTEGER, "
                + COL_TRANS_CAT_ID + " INTEGER, "
                + COL_TRANS_AMOUNT + " INTEGER NOT NULL, "
                + COL_TRANS_NOTE + " TEXT, "
                + COL_TRANS_DATE + " TEXT NOT NULL, "
                + COL_TRANS_IMAGE_URI + " TEXT, "
                + "FOREIGN KEY(" + COL_TRANS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + ") ON DELETE CASCADE, "
                + "FOREIGN KEY(" + COL_TRANS_CAT_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COL_ID + ") ON DELETE CASCADE);";

        String CREATE_BUDGETS_TABLE = "CREATE TABLE " + TABLE_BUDGETS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_BUDGET_USER_ID + " INTEGER, "
                + COL_BUDGET_MONTH + " TEXT NOT NULL, "
                + COL_BUDGET_LIMIT + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + COL_BUDGET_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + ") ON DELETE CASCADE);";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
        db.execSQL(CREATE_BUDGETS_TABLE);

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        seedInitialData(db);
    }

    public void seedInitialData(SQLiteDatabase db) {
        // 1. User: diepnguyen / diepthanh01@
        long userId = -1;
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_ID}, COL_USER_NAME + "=?", new String[]{"diepnguyen"}, null, null, null);
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(0);
            ContentValues userUpdate = new ContentValues();
            userUpdate.put(COL_PASSWORD, PasswordUtils.hashPassword("diepthanh01@"));
            userUpdate.put(COL_FULL_NAME, "Nguyễn Văn Điệp");
            db.update(TABLE_USERS, userUpdate, COL_ID + "=?", new String[]{String.valueOf(userId)});
        } else {
            ContentValues userValues = new ContentValues();
            userValues.put(COL_USER_NAME, "diepnguyen");
            userValues.put(COL_PASSWORD, PasswordUtils.hashPassword("diepthanh01@"));
            userValues.put(COL_FULL_NAME, "Nguyễn Văn Điệp");
            userValues.put(COL_CREATED_AT, "2025-01-01");
            userId = db.insert(TABLE_USERS, null, userValues);
        }
        cursor.close();

        if (userId == -1) return;

        // 2. Categories
        Map<String, Long> catMap = new HashMap<>();
        Cursor catCursor = db.query(TABLE_CATEGORIES, new String[]{COL_ID, COL_CAT_NAME}, COL_CAT_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        while (catCursor.moveToNext()) {
            catMap.put(catCursor.getString(1), catCursor.getLong(0));
        }
        catCursor.close();

        String[][] defaultCats = {
                {"Lương", Category.TYPE_INCOME, "ic_salary"},
                {"Thưởng", Category.TYPE_INCOME, "ic_bonus"},
                {"Học bổng", Category.TYPE_INCOME, "ic_scholarship"},
                {"Thu nhập khác", Category.TYPE_INCOME, "ic_other_income"},
                {"Ăn uống", Category.TYPE_EXPENSE, "ic_food"},
                {"Di chuyển", Category.TYPE_EXPENSE, "ic_transport"},
                {"Mua sắm", Category.TYPE_EXPENSE, "ic_shopping"},
                {"Giải trí", Category.TYPE_EXPENSE, "ic_entertainment"},
                {"Học tập", Category.TYPE_EXPENSE, "ic_study"},
                {"Sức khỏe", Category.TYPE_EXPENSE, "ic_food"},
                {"Khác", Category.TYPE_EXPENSE, "ic_other"}
        };

        for (String[] cat : defaultCats) {
            if (!catMap.containsKey(cat[0])) {
                long catId = insertCategory(db, userId, cat[0], cat[1], cat[2]);
                catMap.put(cat[0], catId);
            }
        }

        long catSalary = getCatId(catMap, "Lương", 1L);
        long catBonus = getCatId(catMap, "Thưởng", 2L);
        long catFood = getCatId(catMap, "Ăn uống", 5L);
        long catTransport = getCatId(catMap, "Di chuyển", 6L);
        long catShopping = getCatId(catMap, "Mua sắm", 7L);
        long catEntertainment = getCatId(catMap, "Giải trí", 8L);
        long catStudy = getCatId(catMap, "Học tập", 9L);

        // 3. Transactions check
        Cursor tCursor = db.query(TABLE_TRANSACTIONS, new String[]{COL_ID}, COL_TRANS_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        int tCount = tCursor.getCount();
        tCursor.close();

        if (tCount < 10) {
            // Seed transactions for 2026-10 (matching Figma)
            insertTransaction(db, userId, catFood, 1200000, "Ăn tối cùng gia đình", "2026-10-28", null);
            insertTransaction(db, userId, catSalary, 12000000, "Tiền lương tháng 10", "2026-10-25", null);
            insertTransaction(db, userId, catShopping, 1500000, "Mua sắm quần áo", "2026-10-20", null);
            insertTransaction(db, userId, catTransport, 300000, "Đổ xăng xe máy", "2026-10-18", null);
            insertTransaction(db, userId, catBonus, 2500000, "Thưởng dự án", "2026-10-15", null);
            insertTransaction(db, userId, catStudy, 1000000, "Khóa học lập trình", "2026-10-10", null);
            insertTransaction(db, userId, catFood, 850000, "Đi siêu thị gia đình", "2026-10-06", null);
            insertTransaction(db, userId, catEntertainment, 300000, "Xem phim & cafe cuối tuần", "2026-10-02", null);

            // Seed transactions for 2026-11
            insertTransaction(db, userId, catSalary, 15000000, "Nhận lương tháng 11", "2026-11-01", null);
            insertTransaction(db, userId, catBonus, 3000000, "Thưởng hiệu quả công việc", "2026-11-01", null);
            insertTransaction(db, userId, catFood, 180000, "Ăn trưa văn phòng", "2026-11-06", null);
            insertTransaction(db, userId, catTransport, 120000, "Đặt Grabcar", "2026-11-05", null);
            insertTransaction(db, userId, catShopping, 650000, "Mua đồ gia dụng", "2026-11-04", null);
            insertTransaction(db, userId, catFood, 350000, "Cafe & điểm tâm sáng", "2026-11-02", null);

            // Seed transactions for Current Month dynamically if different from 2026-10 and 2026-11
            Calendar cal = Calendar.getInstance();
            int curYear = cal.get(Calendar.YEAR);
            int curMonth = cal.get(Calendar.MONTH) + 1;
            String curMonthStr = String.format(Locale.getDefault(), "%04d-%02d", curYear, curMonth);

            if (!curMonthStr.equals("2026-10") && !curMonthStr.equals("2026-11")) {
                insertTransaction(db, userId, catSalary, 15000000, "Nhận lương tháng " + curMonth, String.format(Locale.getDefault(), "%s-01", curMonthStr), null);
                insertTransaction(db, userId, catBonus, 2500000, "Thưởng dự án", String.format(Locale.getDefault(), "%s-05", curMonthStr), null);
                insertTransaction(db, userId, catFood, 1200000, "Ăn tối cùng gia đình", String.format(Locale.getDefault(), "%s-02", curMonthStr), null);
                insertTransaction(db, userId, catShopping, 1500000, "Mua sắm đồ dùng", String.format(Locale.getDefault(), "%s-03", curMonthStr), null);
                insertTransaction(db, userId, catTransport, 300000, "Đổ xăng xe máy", String.format(Locale.getDefault(), "%s-04", curMonthStr), null);
                insertTransaction(db, userId, catStudy, 1000000, "Khóa học trực tuyến", String.format(Locale.getDefault(), "%s-06", curMonthStr), null);
                insertTransaction(db, userId, catFood, 850000, "Đi siêu thị gia đình", String.format(Locale.getDefault(), "%s-07", curMonthStr), null);
                insertTransaction(db, userId, catEntertainment, 300000, "Xem phim & cafe cuối tuần", String.format(Locale.getDefault(), "%s-08", curMonthStr), null);
            }

            // 4. Budgets
            setBudgetInternal(db, userId, "2026-10", 6000000);
            setBudgetInternal(db, userId, "2026-11", 6000000);
            setBudgetInternal(db, userId, curMonthStr, 6000000);
        }
    }

    private long getCatId(Map<String, Long> catMap, String name, long defaultId) {
        Long val = catMap.get(name);
        return val != null ? val : defaultId;
    }

    private void setBudgetInternal(SQLiteDatabase db, long userId, String monthYear, long limitAmount) {
        ContentValues cv = new ContentValues();
        cv.put(COL_BUDGET_USER_ID, userId);
        cv.put(COL_BUDGET_MONTH, monthYear);
        cv.put(COL_BUDGET_LIMIT, limitAmount);

        Cursor c = db.query(TABLE_BUDGETS, new String[]{COL_ID},
                COL_BUDGET_USER_ID + "=? AND " + COL_BUDGET_MONTH + "=?",
                new String[]{String.valueOf(userId), monthYear}, null, null, null);
        if (c.moveToFirst()) {
            db.update(TABLE_BUDGETS, cv, COL_BUDGET_USER_ID + "=? AND " + COL_BUDGET_MONTH + "=?",
                    new String[]{String.valueOf(userId), monthYear});
        } else {
            db.insert(TABLE_BUDGETS, null, cv);
        }
        c.close();
    }

    private long insertCategory(SQLiteDatabase db, long userId, String name, String type, String icon) {
        ContentValues cv = new ContentValues();
        cv.put(COL_CAT_USER_ID, userId);
        cv.put(COL_CAT_NAME, name);
        cv.put(COL_CAT_TYPE, type);
        cv.put(COL_CAT_ICON, icon);
        return db.insert(TABLE_CATEGORIES, null, cv);
    }

    private void insertTransaction(SQLiteDatabase db, long userId, long categoryId, long amount, String note, String date, String imageUri) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TRANS_USER_ID, userId);
        cv.put(COL_TRANS_CAT_ID, categoryId);
        cv.put(COL_TRANS_AMOUNT, amount);
        cv.put(COL_TRANS_NOTE, note);
        cv.put(COL_TRANS_DATE, date);
        cv.put(COL_TRANS_IMAGE_URI, imageUri);
        db.insert(TABLE_TRANSACTIONS, null, cv);
    }
}
