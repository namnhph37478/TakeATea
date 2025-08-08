package com.example.takeatea.fragment.Admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.takeatea.R;
import com.example.takeatea.dao.ContactDAO;
import com.example.takeatea.dao.UserDAO;
import com.example.takeatea.model.Contact;
import com.example.takeatea.model.User;

import java.util.List;

public class ChiTietKhachHangActivity extends AppCompatActivity {

    private TextView tvTen, tvEmail, tvPhone, tvAddress, tvYKien;
    private UserDAO userDAO;
    private ContactDAO contactDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_khach_hang);

        tvTen = findViewById(R.id.tvTen);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvYKien = findViewById(R.id.tvYKien);

        int userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Thiếu userId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userDAO = new UserDAO(this);
        contactDAO = new ContactDAO(this);

        // Thông tin khách hàng
        User u = userDAO.getUserById(userId);
        if (u != null) {
            tvTen.setText(u.getFullname());
            tvEmail.setText("Email: " + nullSafe(u.getEmail()));
            tvPhone.setText("SĐT: " + nullSafe(u.getPhone()));
            tvAddress.setText("Địa chỉ: " + nullSafe(u.getAddress()));
        }

        // === LẤY Ý KIẾN TỪ KHÁCH HÀNG (isFromUser = 1) ===
        // Cách 1: dùng hàm mới getUserOpinions() (tối ưu)
        List<Contact> contacts = contactDAO.getUserOpinions(userId);

        // Nếu bạn chưa thêm hàm mới ở DAO thì có thể dùng:
        // List<Contact> contacts = contactDAO.getByUserId(userId);

        StringBuilder sb = new StringBuilder();
        for (Contact c : contacts) {
            // Chỉ lấy tin nhắn do USER gửi
            if (c.isFromUser()) {
                sb.append("- ").append(c.getMessage()).append("\n");
            }
        }
        tvYKien.setText(sb.length() == 0 ? "Chưa có ý kiến từ khách" : sb.toString());
    }

    private String nullSafe(String s) {
        return s == null ? "" : s;
    }
}
