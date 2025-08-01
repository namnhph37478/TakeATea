package com.example.takeatea.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.takeatea.db.DbHelper;
import com.example.takeatea.model.Order;

import java.util.ArrayList;

public class OrderDAO {
    private DbHelper dbHelper;

    public OrderDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    // Thêm đơn hàng
    public boolean insert(Order order) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("userId", order.getUserId());
        values.put("orderDate", order.getOrderDate());
        values.put("totalAmount", order.getTotalAmount());
        values.put("status", order.getStatus());

        long result = db.insert("OrderTable", null, values);
        db.close();
        return result != -1;
    }

    // Cập nhật đơn hàng
    public boolean update(Order order) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("userId", order.getUserId());
        values.put("orderDate", order.getOrderDate());
        values.put("totalAmount", order.getTotalAmount());
        values.put("status", order.getStatus());

        int result = db.update("OrderTable", values, "id = ?", new String[]{String.valueOf(order.getId())});
        db.close();
        return result > 0;
    }

    // Xoá đơn hàng
    public boolean delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("OrderTable", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // Lấy tất cả đơn hàng
    public ArrayList<Order> getAll() {
        ArrayList<Order> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM OrderTable", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(getOrderFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Lấy đơn hàng theo userId
    public ArrayList<Order> getByUserId(int userId) {
        ArrayList<Order> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM OrderTable WHERE userId = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(getOrderFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Lấy đơn hàng theo id
    public Order getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM OrderTable WHERE id = ?", new String[]{String.valueOf(id)});
        Order order = null;
        if (cursor.moveToFirst()) {
            order = getOrderFromCursor(cursor);
        }
        cursor.close();
        db.close();
        return order;
    }

    // Helper: lấy Order từ Cursor
    private Order getOrderFromCursor(Cursor cursor) {
        return new Order(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                cursor.getString(cursor.getColumnIndexOrThrow("orderDate")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("totalAmount")),
                cursor.getString(cursor.getColumnIndexOrThrow("status"))
        );
    }
}
