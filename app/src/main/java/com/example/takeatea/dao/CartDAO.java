package com.example.takeatea.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.takeatea.db.DbHelper;
import com.example.takeatea.model.Cart;

import java.util.ArrayList;

public class CartDAO {
    private DbHelper dbHelper;

    public CartDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    /**
     * Thêm vào giỏ hoặc cập nhật số lượng nếu đã tồn tại
     */
    public boolean insertOrUpdate(Cart cart) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT quantity FROM CartTable WHERE userId = ? AND productId = ?",
                new String[]{String.valueOf(cart.getUserId()), String.valueOf(cart.getProductId())});

        if (cursor.moveToFirst()) {
            // Đã có → cập nhật số lượng mới
            int oldQty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            int newQty = oldQty + cart.getQuantity();

            ContentValues values = new ContentValues();
            values.put("quantity", newQty);

            int result = db.update("CartTable", values, "userId = ? AND productId = ?",
                    new String[]{String.valueOf(cart.getUserId()), String.valueOf(cart.getProductId())});

            cursor.close();
            db.close();
            return result > 0;
        } else {
            // Chưa có → thêm mới
            ContentValues values = new ContentValues();
            values.put("userId", cart.getUserId());
            values.put("productId", cart.getProductId());
            values.put("quantity", cart.getQuantity());

            long result = db.insert("CartTable", null, values);
            cursor.close();
            db.close();
            return result != -1;
        }
    }

    /**
     * Lấy danh sách sản phẩm trong giỏ theo userId
     */
    public ArrayList<Cart> getCartByUserId(int userId) {
        ArrayList<Cart> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM CartTable WHERE userId = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(new Cart(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("productId")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ
     */
    public boolean updateQuantity(int userId, int productId, int newQuantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);

        int result = db.update("CartTable", values, "userId = ? AND productId = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        db.close();
        return result > 0;
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng
     */
    public boolean deleteItem(int userId, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("CartTable", "userId = ? AND productId = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        db.close();
        return result > 0;
    }

    /**
     * Xóa toàn bộ giỏ hàng của người dùng
     */
    public boolean clearCart(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("CartTable", "userId = ?", new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }
}
