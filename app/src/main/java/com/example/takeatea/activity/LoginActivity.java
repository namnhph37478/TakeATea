package com.example.takeatea.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.takeatea.R;
import com.example.takeatea.dao.UserDAO;
import com.example.takeatea.dialog.ForgotPasswordDialog;
import com.example.takeatea.model.User;
import com.example.takeatea.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private ImageButton btnTogglePassword;
    private boolean isPasswordVisible = false;
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
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        chkRemember = findViewById(R.id.chkRemember);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        userDAO = new UserDAO(this);
        sharedPreferences = getSharedPreferences("USER_FILE", Context.MODE_PRIVATE);

        // Thiết lập chế độ ẩn hiện mật khẩu
        btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Ẩn mật khẩu
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_eye_closed);
            } else {
                // Hiện mật khẩu
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_eye_open);
            }
            isPasswordVisible = !isPasswordVisible;
            // Di chuyển con trỏ về cuối văn bản
            edtPassword.setSelection(edtPassword.getText().length());
        });

        // Load thông tin nếu đã ghi nhớ
        loadRememberedUser();

        // Sự kiện đăng nhập
        btnLogin.setOnClickListener(v -> login());

        // Sự kiện mở trang đăng ký
        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Quên mật khẩu
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

            // 🔐 Tạo phiên đăng nhập
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.createLoginSession(user.getId(), user.getUsername(), user.getRole());

            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

            // 👉 Điều hướng theo vai trò
            if ("admin".equalsIgnoreCase(user.getRole())) {
                startActivity(new Intent(this, MainAdminActivity.class));
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
