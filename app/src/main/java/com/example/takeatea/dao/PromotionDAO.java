package com.example.takeatea.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.takeatea.db.DbHelper;
import com.example.takeatea.model.Promotion;

import java.util.ArrayList;

public class PromotionDAO {
    private final DbHelper dbHelper;

    public PromotionDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    // Thêm khuyến mãi
    public long insert(Promotion promotion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", promotion.getCode());
        values.put("discountPercent", promotion.getDiscountPercent());
        return db.insert("Promotion", null, values);
    }

    // Cập nhật khuyến mãi
    public int update(Promotion promotion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", promotion.getCode());
        values.put("discountPercent", promotion.getDiscountPercent());
        return db.update("Promotion", values, "id = ?", new String[]{String.valueOf(promotion.getId())});
    }

    // Xoá khuyến mãi theo id
    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("Promotion", "id = ?", new String[]{String.valueOf(id)});
    }

    // Lấy toàn bộ danh sách khuyến mãi
    public ArrayList<Promotion> getAll() {
        ArrayList<Promotion> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Promotion", null);

        if (cursor.moveToFirst()) {
            do {
                Promotion promotion = new Promotion(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("code")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("discountPercent"))
                );
                list.add(promotion);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    // Lấy khuyến mãi theo ID
    public Promotion getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Promotion WHERE id = ?", new String[]{String.valueOf(id)});

        if (cursor != null && cursor.moveToFirst()) {
            Promotion promotion = new Promotion(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("code")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("discountPercent"))
            );
            cursor.close();
            return promotion;
        }

        return null;
    }

    // Lấy khuyến mãi theo code
    public Promotion getByCode(String code) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Promotion WHERE code = ?", new String[]{code});

        if (cursor != null && cursor.moveToFirst()) {
            Promotion promotion = new Promotion(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("code")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("discountPercent"))
            );
            cursor.close();
            return promotion;
        }

        return null;
    }
}
