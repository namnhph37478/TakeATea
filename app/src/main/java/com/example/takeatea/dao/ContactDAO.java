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

    public long insert(Contact contact) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", contact.getUserId());
        values.put("message", contact.getMessage());
        values.put("createdAt", contact.getCreatedAt());
        values.put("isFromUser", contact.isFromUser() ? 1 : 0);
        return db.insert("Contact", null, values);
    }

    // Giữ nguyên: lấy toàn bộ message của user (cả bot và user)
    public ArrayList<Contact> getByUserId(int userId) {
        ArrayList<Contact> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Contact WHERE userId = ? ORDER BY createdAt ASC",
                new String[]{String.valueOf(userId)}
        );
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                contact.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                contact.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("message")));
                contact.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
                contact.setFromUser(cursor.getInt(cursor.getColumnIndexOrThrow("isFromUser")) == 1);
                list.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // ★ MỚI: chỉ lấy ý kiến từ KHÁCH HÀNG (isFromUser = 1)
    public ArrayList<Contact> getUserOpinions(int userId) {
        ArrayList<Contact> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Contact WHERE userId=? AND isFromUser=1 ORDER BY createdAt ASC",
                new String[]{String.valueOf(userId)}
        );
        if (cursor.moveToFirst()) {
            do {
                Contact c = new Contact();
                c.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                c.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                c.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("message")));
                c.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
                c.setFromUser(true);
                list.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
