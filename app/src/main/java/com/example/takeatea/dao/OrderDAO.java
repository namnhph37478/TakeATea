package com.example.takeatea.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.takeatea.db.DbHelper;
import com.example.takeatea.model.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderDAO {

    private final DbHelper dbHelper;

    public OrderDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    // ✅ Thêm đơn hàng mới
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

    // ✅ Lấy ID đơn hàng vừa tạo gần nhất (cho user đang đặt)
    public int getLastOrderId() {
        int orderId = -1;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM OrderTable ORDER BY id DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            orderId = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return orderId;
    }

    // ✅ Cập nhật đơn hàng
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

    // ✅ Xoá đơn hàng
    public boolean delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("OrderTable", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // ✅ Lấy tất cả đơn hàng (Admin)
    public ArrayList<Order> getAll() {
        ArrayList<Order> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM OrderTable ORDER BY id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(getOrderFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // ✅ Lấy đơn hàng theo userId (User)
    public ArrayList<Order> getByUserId(int userId) {
        ArrayList<Order> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM OrderTable WHERE userId = ? ORDER BY id DESC",
                new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(getOrderFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // ✅ Lấy đơn hàng theo id
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

    // Đếm đơn theo ngày (orderDate TEXT 'yyyy-MM-dd' hoặc có kèm giờ)
    public int countOrdersByDateText(String yyyyMMdd) {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // an toàn với chuỗi có kèm giờ: so sánh substr(orderDate,1,10)
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM OrderTable WHERE substr(orderDate,1,10) = ?",
                new String[]{yyyyMMdd}
        );
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        db.close();
        return count;
    }

    // Tổng doanh thu theo ngày (totalAmount REAL)
    public double sumRevenueByDateText(String yyyyMMdd) {
        double sum = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(totalAmount),0) FROM OrderTable WHERE substr(orderDate,1,10) = ?",
                new String[]{yyyyMMdd}
        );
        if (c.moveToFirst()) sum = c.getDouble(0);
        c.close();
        db.close();
        return sum;
    }

    // Map doanh thu theo ngày trong khoảng [startDate, endDate], định dạng yyyy-MM-dd
    public Map<String, Double> sumRevenueGroupByDate(String startDate, String endDate) {
        Map<String, Double> map = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT substr(orderDate,1,10) AS d, IFNULL(SUM(totalAmount),0) " +
                        "FROM OrderTable " +
                        "WHERE substr(orderDate,1,10) BETWEEN ? AND ? " +
                        "GROUP BY substr(orderDate,1,10) " +
                        "ORDER BY d",
                new String[]{startDate, endDate}
        );
        while (c.moveToNext()) {
            map.put(c.getString(0), c.getDouble(1));
        }
        c.close();
        db.close();
        return map;
    }



    // Helper: lấy đối tượng Order từ Cursor
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
