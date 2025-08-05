package com.example.takeatea.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.takeatea.R;
import com.example.takeatea.dao.UserDAO;
import com.example.takeatea.dialog.ForgotPasswordDialog;
import com.example.takeatea.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private CheckBox chkRemember;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegisterLink;

    private SharedPreferences sharedPreferences;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ view
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        chkRemember = findViewById(R.id.chkRemember);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        userDAO = new UserDAO(this);
        sharedPreferences = getSharedPreferences("USER_FILE", Context.MODE_PRIVATE);

        // Load thông tin nếu đã lưu
        loadRememberedUser();

        // Xử lý nút Đăng nhập
        btnLogin.setOnClickListener(v -> login());

        //xu ly "Đăng ký"
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý "Quên mật khẩu"
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(v -> {
            ForgotPasswordDialog dialog = new ForgotPasswordDialog(LoginActivity.this);
            dialog.show();
        });

    }

    private void login() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }



        User user = userDAO.login(username, password);
        if (user != null) {
            // Ghi nhớ nếu có chọn
            if (chkRemember.isChecked()) {
                rememberUser(username, password);
            } else {
                clearRememberedUser();
            }

            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

            // Điều hướng theo vai trò
            if ("admin".equalsIgnoreCase(user.getRole())) {
                startActivity(new Intent(this, MainAdminActivity.class)); // nếu có layout riêng cho admin
            } else {
                startActivity(new Intent(this, MainUserActivity.class));
            }

            finish(); // kết thúc LoginActivity
        } else {
            Toast.makeText(this, "Tài khoản hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
        }
    }


    private void rememberUser(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USERNAME", username);
        editor.putString("PASSWORD", password);
        editor.putBoolean("REMEMBER", true);
        editor.apply();
    }

    private void clearRememberedUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void loadRememberedUser() {
        boolean isRemembered = sharedPreferences.getBoolean("REMEMBER", false);
        if (isRemembered) {
            edtUsername.setText(sharedPreferences.getString("USERNAME", ""));
            edtPassword.setText(sharedPreferences.getString("PASSWORD", ""));
            chkRemember.setChecked(true);
        }
    }
}
