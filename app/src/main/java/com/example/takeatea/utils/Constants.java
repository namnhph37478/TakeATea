package com.example.takeatea.utils;

public class Constants {

    // Tên Database
    public static final String DATABASE_NAME = "TakeATea.db";
    public static final int DATABASE_VERSION = 1;

    // Vai trò người dùng
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";

    // SharedPreferences
    public static final String PREF_NAME = "TakeATeaPrefs";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_FULL_NAME = "full_name";
    public static final String KEY_ROLE = "role";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // Các mã intent extra
    public static final String EXTRA_PRODUCT_ID = "product_id";
    public static final String EXTRA_ORDER_ID = "order_id";

    // Mặc định nếu không có gì
    public static final int DEFAULT_ID = -1;

    // Giảm giá tối đa (nếu cần)
    public static final int MAX_DISCOUNT = 100;

    // Định dạng thời gian
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm";
}
