package com.example.takeatea.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.takeatea.db.DbHelper;
import com.example.takeatea.model.OrderDetail;

import java.util.ArrayList;

public class OrderDetailDAO {
    private DbHelper dbHelper;

    public OrderDetailDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    // Thêm chi tiết đơn hàng
    public boolean insert(OrderDetail detail) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("orderId", detail.getOrderId());
        values.put("productId", detail.getProductId());
        values.put("quantity", detail.getQuantity());
        values.put("price", detail.getPrice());

        long result = db.insert("OrderDetailTable", null, values);
        db.close();
        return result != -1;
    }

    // Lấy toàn bộ chi tiết của 1 đơn hàng
    public ArrayList<OrderDetail> getByOrderId(int orderId) {
        ArrayList<OrderDetail> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM OrderDetailTable WHERE orderId = ?", new String[]{String.valueOf(orderId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(getFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Xoá chi tiết đơn hàng theo id
    public boolean delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("OrderDetailTable", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // Xoá tất cả chi tiết theo orderId
    public boolean deleteByOrderId(int orderId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("OrderDetailTable", "orderId = ?", new String[]{String.valueOf(orderId)});
        db.close();
        return result > 0;
    }

    // Helper: chuyển Cursor thành đối tượng OrderDetail
    private OrderDetail getFromCursor(Cursor cursor) {
        return new OrderDetail(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("orderId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("productId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
        );
    }
}
