package com.example.takeatea.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.takeatea.R;
import com.example.takeatea.dao.UserDAO;
import com.example.takeatea.model.User;

public class RegisterActivity extends AppCompatActivity {
    EditText edtFullName, edtUsername, edtPassword, edtEmail, edtAddress, edtphone;
    Button btnRegister;
    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        edtFullName = findViewById(R.id.edtFullName);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        edtphone = findViewById(R.id.edtphone);
        btnRegister = findViewById(R.id.btnRegister);

        // Khởi tạo DAO
        userDAO = new UserDAO(this);

        btnRegister.setOnClickListener(v -> {
            // Lấy dữ liệu người dùng nhập
            String fullname = edtFullName.getText().toString().trim();
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String phone = edtphone.getText().toString().trim();

            // Kiểm tra rỗng
            if (fullname.isEmpty() || username.isEmpty() || password.isEmpty()
                    || email.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ tất cả thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng User (role mặc định là "user")
            User user = new User(username, password, fullname, phone, email, address, "user");

            // Thêm vào DB
            boolean result = userDAO.insertUser(user);

            if (result) {
                Toast.makeText(this, "Đăng ký thành công! Mời bạn đăng nhập.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Đăng ký thất bại. Tên đăng nhập đã tồn tại?", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
