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
        // Bảng User
        db.execSQL("CREATE TABLE User (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "fullname TEXT, " +
                "phone TEXT, " +
                "role TEXT NOT NULL)");

        // Bảng Product
        db.execSQL("CREATE TABLE Product (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "description TEXT, " +
                "image TEXT, " +
                "categoryId INTEGER, " +
                "quantity INTEGER DEFAULT 0, " +
                "status INTEGER DEFAULT 1, " +
                "rating REAL DEFAULT 0)");

        // Bảng Promotion
        db.execSQL("CREATE TABLE Promotion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT UNIQUE, " +
                "discountPercent INTEGER)");

        // Bảng PromotionProduct
        db.execSQL("CREATE TABLE PromotionProduct (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "promotionId INTEGER, " +
                "productId INTEGER, " +
                "FOREIGN KEY (promotionId) REFERENCES Promotion(id), " +
                "FOREIGN KEY (productId) REFERENCES Product(id))");

        // Bảng CartTable
        db.execSQL("CREATE TABLE CartTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER REFERENCES User(id), " +
                "productId INTEGER REFERENCES Product(id), " +
                "quantity INTEGER)");

        // Bảng OrderTable
        db.execSQL("CREATE TABLE OrderTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER REFERENCES User(id), " +
                "orderDate TEXT, " +
                "totalAmount REAL, " +
                "status TEXT)");

        // Bảng OrderDetailTable
        db.execSQL("CREATE TABLE OrderDetailTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "orderId INTEGER REFERENCES OrderTable(id), " +
                "productId INTEGER REFERENCES Product(id), " +
                "quantity INTEGER, " +
                "price REAL)");

        // Bảng Review
        db.execSQL("CREATE TABLE Review (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER REFERENCES User(id), " +
                "product_id INTEGER REFERENCES Product(id), " +
                "content TEXT, " +
                "rating INTEGER)");

        // Bảng Contact
        db.execSQL("CREATE TABLE Contact (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER REFERENCES User(id), " +
                "subject TEXT, " +
                "message TEXT, " +
                "createdAt TEXT)");

        insertInitialData(db); // Thêm dữ liệu mẫu
    }

    private void insertInitialData(SQLiteDatabase db) {
        // Thêm user mẫu
        db.execSQL("INSERT INTO User (username, password, fullname, phone, role) VALUES " +
                "('admin', 'admin123', 'Quản trị viên', '0123456789', 'admin')," +
                "('user1', 'user123', 'Nguyễn Văn A', '0987654321', 'user')");

        // Thêm sản phẩm mẫu
        db.execSQL("INSERT INTO Product (name, price, description, image, categoryId, quantity, status, rating) VALUES " +
                "('Trà Sữa Truyền Thống', 25000, 'Hương vị truyền thống', 'image_trasua1.png', 1, 100, 1, 4.5)," +
                "('Trà Sữa Matcha', 30000, 'Thơm ngon vị matcha', 'image_trasua2.png', 1, 80, 1, 4.2)");

        // Thêm khuyến mãi mẫu
        db.execSQL("INSERT INTO Promotion (code, discountPercent) VALUES " +
                "('KMHE10', 10)," +
                "('SN20', 20)");

        // Thêm liên kết khuyến mãi với sản phẩm
        db.execSQL("INSERT INTO PromotionProduct (promotionId, productId) VALUES (1, 1), (2, 2)");

        // Thêm đánh giá mẫu
        db.execSQL("INSERT INTO Review (user_id, product_id, content, rating) VALUES " +
                "(2, 1, 'Ngon và vừa miệng', 5)," +
                "(2, 2, 'Hơi ngọt nhưng thơm', 4)");

        // Thêm liên hệ mẫu
        db.execSQL("INSERT INTO Contact (userId, subject, message, createdAt) VALUES " +
                "(2, 'Góp ý ứng dụng', 'Ứng dụng rất dễ sử dụng!', '2025-07-31 10:00:00')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Product");
        db.execSQL("DROP TABLE IF EXISTS Promotion");
        db.execSQL("DROP TABLE IF EXISTS PromotionProduct");
        db.execSQL("DROP TABLE IF EXISTS CartTable");
        db.execSQL("DROP TABLE IF EXISTS OrderTable");
        db.execSQL("DROP TABLE IF EXISTS OrderDetailTable");
        db.execSQL("DROP TABLE IF EXISTS Review");
        db.execSQL("DROP TABLE IF EXISTS Contact");

        onCreate(db); // Tạo lại
    }
}
