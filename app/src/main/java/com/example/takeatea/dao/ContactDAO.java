package com.example.takeatea.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.takeatea.db.DbHelper;
import com.example.takeatea.model.Contact;

import java.util.ArrayList;

public class ContactDAO {
    private final DbHelper dbHelper;

    public ContactDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    // Thêm liên hệ mới
    public long insert(Contact contact) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", contact.getUserId());
        values.put("subject", contact.getSubject());
        values.put("message", contact.getMessage());
        values.put("createdAt", contact.getCreatedAt());

        return db.insert("Contact", null, values);
    }

    // Lấy danh sách liên hệ (toàn bộ)
    public ArrayList<Contact> getAll() {
        ArrayList<Contact> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Contact ORDER BY createdAt DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                contact.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                contact.setSubject(cursor.getString(cursor.getColumnIndexOrThrow("subject")));
                contact.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("message")));
                contact.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
                list.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    // Xoá liên hệ theo ID
    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("Contact", "id = ?", new String[]{String.valueOf(id)});
    }

    // Lấy liên hệ theo userId
    public ArrayList<Contact> getByUserId(int userId) {
        ArrayList<Contact> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Contact WHERE userId = ? ORDER BY createdAt DESC",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                contact.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                contact.setSubject(cursor.getString(cursor.getColumnIndexOrThrow("subject")));
                contact.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("message")));
                contact.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
                list.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
}
