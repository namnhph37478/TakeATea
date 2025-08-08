package com.example.takeatea.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.takeatea.R;
import com.example.takeatea.fragment.Admin.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainAdminActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        bottomNav = findViewById(R.id.bottom_nav_admin);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Load Fragment mặc định
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_admin, new TrangChuFragment())
                    .commit();
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    selectedFragment = new TrangChuFragment();
                } else if (id == R.id.nav_order) {
                    selectedFragment = new LichSuDonHangFragment();
                } else if (id == R.id.nav_stats) {
                    selectedFragment = new ThongKeFragment();
                } else if (id == R.id.nav_promo) {
                    selectedFragment = new KhuyenMaiFragment();
                } else if (id == R.id.nav_account) {
                    selectedFragment = new TaiKhoanAdminFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_admin, selectedFragment)
                            .commit();
                }

                return true;
            };
}
