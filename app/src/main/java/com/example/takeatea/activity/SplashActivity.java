package com.example.takeatea.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.takeatea.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 5000; // 5 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        // Delay 5 giây rồi chuyển sang LoginActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Sau khi splash xong, chuyển sang màn hình đăng nhập
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Đóng splash screen để không quay lại được
            }
        }, SPLASH_DURATION);
    }
}
