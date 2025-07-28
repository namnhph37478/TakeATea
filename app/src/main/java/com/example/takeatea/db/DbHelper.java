package com.example.takeatea.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TakeATea.db";
    public static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TẠO BẢNG USER
        db.execSQL("CREATE TABLE User (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "fullname TEXT, " +
                "phone TEXT, " +
                "role TEXT NOT NULL)");

        // TẠO BẢNG SẢN PHẨM
        db.execSQL("CREATE TABLE Product (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "image TEXT, " +
                "description TEXT)");

        // TẠO BẢNG KHUYẾN MÃI
        db.execSQL("CREATE TABLE Promotion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "discount_percent INTEGER NOT NULL)");

        // TẠO BẢNG LIÊN KẾT SẢN PHẨM - KHUYẾN MÃI
        db.execSQL("CREATE TABLE PromotionProduct (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "product_id INTEGER REFERENCES Product(id), " +
                "promotion_id INTEGER REFERENCES Promotion(id))");

        // TẠO BẢNG GIỎ HÀNG
        db.execSQL("CREATE TABLE Cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER REFERENCES User(id), " +
                "product_id INTEGER REFERENCES Product(id), " +
                "quantity INTEGER NOT NULL)");

        // TẠO BẢNG ĐƠN HÀNG
        db.execSQL("CREATE TABLE OrderTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER REFERENCES User(id), " +
                "order_date TEXT, " +
                "total REAL)");

        // TẠO BẢNG CHI TIẾT ĐƠN HÀNG
        db.execSQL("CREATE TABLE OrderDetail (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER REFERENCES OrderTable(id), " +
                "product_id INTEGER REFERENCES Product(id), " +
                "quantity INTEGER, " +
                "price REAL)");

        // TẠO BẢNG ĐÁNH GIÁ
        db.execSQL("CREATE TABLE Review (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER REFERENCES User(id), " +
                "product_id INTEGER REFERENCES Product(id), " +
                "content TEXT, " +
                "rating INTEGER)");

        // TẠO BẢNG LIÊN HỆ
        db.execSQL("CREATE TABLE Contact (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER REFERENCES User(id), " +
                "message TEXT)");

        insertInitialData(db); // Chèn dữ liệu mẫu
    }

    private void insertInitialData(SQLiteDatabase db) {
        // Thêm admin và người dùng mẫu
        db.execSQL("INSERT INTO User (username, password, fullname, phone, role) VALUES " +
                "('admin', 'admin123', 'Quản trị viên', '0123456789', 'admin')," +
                "('user1', 'user123', 'Nguyễn Văn A', '0987654321', 'user')");

        // Thêm sản phẩm mẫu
        db.execSQL("INSERT INTO Product (name, price, image, description) VALUES " +
                "('Trà Sữa Truyền Thống', 25000, 'image_trasua1.png', 'Hương vị truyền thống')," +
                "('Trà Sữa Matcha', 30000, 'image_trasua2.png', 'Thơm ngon vị matcha')");

        // Thêm khuyến mãi mẫu
        db.execSQL("INSERT INTO Promotion (title, discount_percent) VALUES " +
                "('Khuyến mãi hè', 10)," +
                "('Giảm giá sinh nhật', 20)");

        // Liên kết khuyến mãi với sản phẩm
        db.execSQL("INSERT INTO PromotionProduct (product_id, promotion_id) VALUES (1, 1), (2, 2)");

        // Thêm đánh giá mẫu
        db.execSQL("INSERT INTO Review (user_id, product_id, content, rating) VALUES " +
                "(2, 1, 'Ngon và vừa miệng', 5)," +
                "(2, 2, 'Hơi ngọt nhưng thơm', 4)");

        // Thêm liên hệ mẫu
        db.execSQL("INSERT INTO Contact (user_id, message) VALUES " +
                "(2, 'Ứng dụng rất dễ sử dụng!')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xoá bảng cũ nếu có
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Product");
        db.execSQL("DROP TABLE IF EXISTS Promotion");
        db.execSQL("DROP TABLE IF EXISTS PromotionProduct");
        db.execSQL("DROP TABLE IF EXISTS Cart");
        db.execSQL("DROP TABLE IF EXISTS OrderTable");
        db.execSQL("DROP TABLE IF EXISTS OrderDetail");
        db.execSQL("DROP TABLE IF EXISTS Review");
        db.execSQL("DROP TABLE IF EXISTS Contact");

        onCreate(db); // Tạo lại bảng
    }
}
