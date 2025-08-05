package com.example.takeatea.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.takeatea.db.DbHelper;
import com.example.takeatea.model.User;

import java.util.ArrayList;

public class UserDAO {
    private SQLiteDatabase db;

    private DbHelper dbHelper;

    public UserDAO(Context context) {
        this.dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Đăng nhập
    public User login(String username, String password) {
        String sql = "SELECT * FROM User WHERE username=? AND password=?";
        Cursor cursor = db.rawQuery(sql, new String[]{username, password});
        if (cursor.moveToFirst()) {
            return getUserFromCursor(cursor);
        }
        return null;
    }

    // Đăng ký
    public boolean register(User user) {
        ContentValues values = new ContentValues();
        values.put("full_name", user.getFullname());
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("email", user.getEmail());
        values.put("phone", user.getPhone());
        values.put("address", user.getAddress());
        values.put("role", user.getRole());

        long result = db.insert("User", null, values);
        return result != -1;
    }

    // Lấy user theo ID
    public User getUserById(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE id=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            return getUserFromCursor(cursor);
        }
        return null;
    }

    // Cập nhật thông tin
    public boolean updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put("full_name", user.getFullname());
        values.put("password", user.getPassword());
        values.put("email", user.getEmail());
        values.put("phone", user.getPhone());
        values.put("address", user.getAddress());

        int result = db.update("User", values, "id=?", new String[]{String.valueOf(user.getId())});
        return result > 0;
    }

    // Lấy tất cả user (chỉ dùng cho admin)
    public ArrayList<User> getAllUsers() {
        ArrayList<User> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM User", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(getUserFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        return list;
    }

    // Xoá user (nếu cần)
    public boolean deleteUser(int id) {
        int result = db.delete("User", "id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Hàm tiện ích đọc dữ liệu từ Cursor
    private User getUserFromCursor(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        user.setFullname(cursor.getString(cursor.getColumnIndexOrThrow("fullname")));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
        user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
        user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
        return user;
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("fullname", user.getFullname());
        values.put("phone", user.getPhone());
        values.put("role", user.getRole());

        long result = db.insert("User", null, values);
        return result != -1;
    }

    public User getUserByUsername(String username) {
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE username = ?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
            user.setFullname(cursor.getString(cursor.getColumnIndexOrThrow("fullname")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }



}
