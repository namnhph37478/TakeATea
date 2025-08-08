package com.example.takeatea.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.takeatea.db.DbHelper;
import com.example.takeatea.model.OrderDetail;

import java.util.ArrayList;

public class OrderDetailDAO {

    private final DbHelper dbHelper;

    public OrderDetailDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    // ======================= CÁC HÀM CŨ (GIỮ NGUYÊN) =======================

    /** Thêm chi tiết đơn hàng */
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

    /** Lấy toàn bộ chi tiết đơn hàng theo orderId */
    public ArrayList<OrderDetail> getByOrderId(int orderId) {
        ArrayList<OrderDetail> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM OrderDetailTable WHERE orderId = ?",
                new String[]{String.valueOf(orderId)}
        );
        if (cursor.moveToFirst()) {
            do {
                list.add(getFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    /** Xóa chi tiết đơn hàng theo ID dòng */
    public boolean delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("OrderDetailTable", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    /** Xóa tất cả chi tiết theo orderId */
    public boolean deleteByOrderId(int orderId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("OrderDetailTable", "orderId = ?", new String[]{String.valueOf(orderId)});
        db.close();
        return result > 0;
    }

    /** Helper: tạo OrderDetail từ Cursor */
    private OrderDetail getFromCursor(Cursor cursor) {
        return new OrderDetail(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("orderId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("productId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
        );
    }

    // ======================= CÁC HÀM MỚI (THÊM) =======================

    /** DTO nhỏ cho Top sản phẩm bán chạy */
    public static class TopProduct {
        public final int productId;
        public final String name;
        public final int totalSold;

        public TopProduct(int productId, String name, int totalSold) {
            this.productId = productId;
            this.name = name;
            this.totalSold = totalSold;
        }
    }

    /**
     * Lấy Top N sản phẩm bán chạy (tổng quantity) - không lọc ngày
     * Dựa trên OrderDetailTable + Product
     */
    public ArrayList<TopProduct> getTopProducts(int limit) {
        ArrayList<TopProduct> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT p.id AS pid, p.name AS pname, SUM(od.quantity) AS totalSold " +
                        "FROM OrderDetailTable od " +
                        "JOIN Product p ON p.id = od.productId " +
                        "GROUP BY p.id, p.name " +
                        "ORDER BY totalSold DESC " +
                        "LIMIT ?",
                new String[]{String.valueOf(limit)}
        );

        if (c.moveToFirst()) {
            do {
                list.add(new TopProduct(
                        c.getInt(c.getColumnIndexOrThrow("pid")),
                        c.getString(c.getColumnIndexOrThrow("pname")),
                        c.getInt(c.getColumnIndexOrThrow("totalSold"))
                ));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    /**
     * (Tuỳ chọn) Lấy Top N sản phẩm bán chạy theo khoảng ngày (yyyy-MM-dd)
     * Dựa vào OrderTable.orderDate (TEXT) với substr(orderDate,1,10)
     * Không ảnh hưởng code cũ.
     */
    public ArrayList<TopProduct> getTopProductsByDateRange(int limit, String startDate, String endDate) {
        ArrayList<TopProduct> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT p.id AS pid, p.name AS pname, SUM(od.quantity) AS totalSold " +
                        "FROM OrderDetailTable od " +
                        "JOIN Product p ON p.id = od.productId " +
                        "JOIN OrderTable o ON o.id = od.orderId " +
                        "WHERE substr(o.orderDate,1,10) BETWEEN ? AND ? " +
                        "GROUP BY p.id, p.name " +
                        "ORDER BY totalSold DESC " +
                        "LIMIT ?",
                new String[]{startDate, endDate, String.valueOf(limit)}
        );

        if (c.moveToFirst()) {
            do {
                list.add(new TopProduct(
                        c.getInt(c.getColumnIndexOrThrow("pid")),
                        c.getString(c.getColumnIndexOrThrow("pname")),
                        c.getInt(c.getColumnIndexOrThrow("totalSold"))
                ));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }
}
