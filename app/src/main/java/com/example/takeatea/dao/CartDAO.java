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

    // Thêm sản phẩm vào giỏ hàng (nếu đã có thì cập nhật số lượng)
    public boolean addToCart(Cart cart) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Kiểm tra nếu đã có productId + userId thì update
        Cursor cursor = db.rawQuery("SELECT * FROM CartTable WHERE userId = ? AND productId = ?",
                new String[]{String.valueOf(cart.getUserId()), String.valueOf(cart.getProductId())});

        if (cursor.moveToFirst()) {
            // Cập nhật quantity
            int oldQty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            ContentValues values = new ContentValues();
            values.put("quantity", oldQty + cart.getQuantity());

            int result = db.update("CartTable", values, "userId = ? AND productId = ?",
                    new String[]{String.valueOf(cart.getUserId()), String.valueOf(cart.getProductId())});
            cursor.close();
            db.close();
            return result > 0;
        } else {
            // Thêm mới
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

    // Lấy danh sách giỏ hàng theo userId
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

    // Cập nhật số lượng sản phẩm trong giỏ
    public boolean updateQuantity(int userId, int productId, int newQuantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);

        int result = db.update("CartTable", values, "userId = ? AND productId = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        db.close();
        return result > 0;
    }

    // Xoá sản phẩm khỏi giỏ
    public boolean deleteItem(int userId, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("CartTable", "userId = ? AND productId = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        db.close();
        return result > 0;
    }

    // Xoá toàn bộ giỏ hàng theo userId
    public boolean clearCart(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("CartTable", "userId = ?", new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }
}
