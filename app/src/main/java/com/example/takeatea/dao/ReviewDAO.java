package com.example.takeatea.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.takeatea.db.DbHelper;
import com.example.takeatea.model.Review;

import java.util.ArrayList;

public class ReviewDAO {
    private final DbHelper dbHelper;

    public ReviewDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    // Thêm đánh giá
    public boolean insertReview(Review review) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", review.getUserId());
        values.put("product_id", review.getProductId());
        values.put("content", review.getComment());
        values.put("rating", review.getRating());
        values.put("date", review.getDate()); // Thêm ngày

        long result = db.insert("Review", null, values);
        db.close();
        return result != -1;
    }

    // Lấy danh sách đánh giá theo sản phẩm
    public ArrayList<Review> getReviewsByProduct(int productId) {
        ArrayList<Review> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Review WHERE product_id = ?",
                new String[]{String.valueOf(productId)});

        if (cursor.moveToFirst()) {
            do {
                Review r = new Review(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("product_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("rating")),
                        cursor.getString(cursor.getColumnIndexOrThrow("content")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date"))
                );
                list.add(r);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean deleteReview(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("Review", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public double getAverageRating(int productId) {
        double avg = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(rating) AS avgRating FROM Review WHERE product_id = ?",
                new String[]{String.valueOf(productId)});
        if (cursor.moveToFirst()) {
            avg = cursor.getDouble(cursor.getColumnIndexOrThrow("avgRating"));
        }
        cursor.close();
        db.close();
        return avg;
    }
}
