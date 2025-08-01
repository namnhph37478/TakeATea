package com.example.takeatea.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.takeatea.db.DbHelper;
import com.example.takeatea.model.Product;

import java.util.ArrayList;

public class ProductDAO {
    private DbHelper dbHelper;

    public ProductDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    // Thêm sản phẩm
    public boolean insert(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", product.getName());
        values.put("price", product.getPrice());
        values.put("description", product.getDescription());
        values.put("image", product.getImage());
        values.put("categoryId", product.getCategoryId());
        values.put("quantity", product.getQuantity());
        values.put("status", product.getStatus());
        values.put("rating", product.getRating());

        long result = db.insert("Product", null, values);
        db.close();
        return result != -1;
    }

    // Cập nhật sản phẩm
    public boolean update(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", product.getName());
        values.put("price", product.getPrice());
        values.put("description", product.getDescription());
        values.put("image", product.getImage());
        values.put("categoryId", product.getCategoryId());
        values.put("quantity", product.getQuantity());
        values.put("status", product.getStatus());
        values.put("rating", product.getRating());

        int result = db.update("Product", values, "id = ?", new String[]{String.valueOf(product.getId())});
        db.close();
        return result > 0;
    }

    // Xóa sản phẩm
    public boolean delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("Product", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // Lấy danh sách tất cả sản phẩm
    public ArrayList<Product> getAll() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Product", null);
        if (cursor.moveToFirst()) {
            do {
                Product product = getProductFromCursor(cursor);
                list.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Lấy sản phẩm theo ID
    public Product getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Product WHERE id = ?", new String[]{String.valueOf(id)});
        Product product = null;
        if (cursor.moveToFirst()) {
            product = getProductFromCursor(cursor);
        }
        cursor.close();
        db.close();
        return product;
    }

    // Lọc theo danh mục
    public ArrayList<Product> getByCategory(int categoryId) {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Product WHERE categoryId = ?", new String[]{String.valueOf(categoryId)});
        if (cursor.moveToFirst()) {
            do {
                Product product = getProductFromCursor(cursor);
                list.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Tìm kiếm theo tên
    public ArrayList<Product> searchByName(String keyword) {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Product WHERE name LIKE ?", new String[]{"%" + keyword + "%"});
        if (cursor.moveToFirst()) {
            do {
                Product product = getProductFromCursor(cursor);
                list.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Helper method: đọc product từ Cursor
    private Product getProductFromCursor(Cursor cursor) {
        return new Product(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getString(cursor.getColumnIndexOrThrow("image")),
                cursor.getInt(cursor.getColumnIndexOrThrow("categoryId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                cursor.getInt(cursor.getColumnIndexOrThrow("status")),
                cursor.getFloat(cursor.getColumnIndexOrThrow("rating"))
        );
    }
}
