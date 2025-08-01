package com.example.takeatea.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.takeatea.db.DbHelper;

import java.util.ArrayList;

public class PromotionProductDAO {
    private final DbHelper dbHelper;

    public PromotionProductDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    // Thêm quan hệ khuyến mãi - sản phẩm
    public long insert(int promotionId, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("promotionId", promotionId);
        values.put("productId", productId);
        return db.insert("PromotionProduct", null, values);
    }

    // Xoá theo id
    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("PromotionProduct", "id = ?", new String[]{String.valueOf(id)});
    }

    // Xoá theo promotionId
    public int deleteByPromotionId(int promotionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("PromotionProduct", "promotionId = ?", new String[]{String.valueOf(promotionId)});
    }

    // Xoá theo productId
    public int deleteByProductId(int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("PromotionProduct", "productId = ?", new String[]{String.valueOf(productId)});
    }

    // Lấy danh sách productId theo promotionId
    public ArrayList<Integer> getProductIdsByPromotionId(int promotionId) {
        ArrayList<Integer> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT productId FROM PromotionProduct WHERE promotionId = ?", new String[]{String.valueOf(promotionId)});

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getInt(cursor.getColumnIndexOrThrow("productId")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    // Lấy danh sách promotionId theo productId
    public ArrayList<Integer> getPromotionIdsByProductId(int productId) {
        ArrayList<Integer> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT promotionId FROM PromotionProduct WHERE productId = ?", new String[]{String.valueOf(productId)});

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getInt(cursor.getColumnIndexOrThrow("promotionId")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
}
