package com.example.accountingbook;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "account_book.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建用户表
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, role TEXT)");
        // 创建记账记录表
        db.execSQL("CREATE TABLE records (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, amount REAL, category TEXT, date TEXT, note TEXT)");

        // 插入内置管理员
        ContentValues values = new ContentValues();
        values.put("username", "admin");
        values.put("password", "admin");  // 在实际应用中请使用加密方式存储密码
        values.put("role", "admin");
        db.insert("users", null, values);
    }

    public int validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id"}, "username=? AND password=?", new String[]{username, password}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            return userId;
        }

        if (cursor != null) {
            cursor.close();
        }
        return -1;
    }

    public List<Record> getAllRecords() {
        List<Record> recordList = new ArrayList<>();
        String selectQuery = "SELECT * FROM records";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                BigDecimal amount = new BigDecimal(cursor.getString(cursor.getColumnIndexOrThrow("amount")));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));

                Record record = new Record(id, userId,amount, category, date, note);
                recordList.add(record);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return recordList;
    }

    public List<Record> getRecordsByUserId(int userId) {
        List<Record> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("records", null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, "date DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
                @SuppressLint("Range") BigDecimal amount = new BigDecimal(cursor.getString(cursor.getColumnIndex("amount")));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                @SuppressLint("Range") String note = cursor.getString(cursor.getColumnIndex("note"));
                records.add(new Record(id, userId, amount, category, date, note));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return records;
    }

    public void insertRecord(int userId, float amount, String category, String date, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("amount", amount);
        values.put("category", category);
        values.put("date", date);
        values.put("note", note);
        db.insert("records", null, values);
    }

    public void deleteRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("records", "id = ?", new String[]{String.valueOf(id)});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库升级时的操作
    }
}
