package com.example.takeatea.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.takeatea.R;
import com.example.takeatea.fragment.User.GioHangFragment;
import com.example.takeatea.fragment.User.LichSuDonFragment;
import com.example.takeatea.fragment.User.LienHeFragment;
import com.example.takeatea.fragment.User.TaiKhoanFragment;
import com.example.takeatea.fragment.User.TrangChuFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainUserActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Mặc định hiển thị Trang chủ
        loadFragment(new TrangChuFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_trang_chu) {
                loadFragment(new TrangChuFragment());
            } else if (itemId == R.id.menu_gio_hang) {
                loadFragment(new GioHangFragment());
            } else if (itemId == R.id.menu_lich_su) {
                loadFragment(new LichSuDonFragment());
            } else if (itemId == R.id.menu_lien_he) {
                loadFragment(new LienHeFragment());
            } else if (itemId == R.id.menu_tai_khoan) {
                loadFragment(new TaiKhoanFragment());
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_user, fragment)
                .commit();
    }
}
