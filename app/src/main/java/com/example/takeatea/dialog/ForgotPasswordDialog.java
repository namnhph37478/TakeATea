package com.example.takeatea.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takeatea.R;
import com.example.takeatea.dao.UserDAO;
import com.example.takeatea.model.User;

public class ForgotPasswordDialog extends Dialog {

    private EditText edtUsername;
    private EditText edtPhone;
    private TextView tvRecoveredPassword;
    private Button btnRecover;
    private UserDAO userDAO;

    public ForgotPasswordDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_forgot_password);

        edtUsername = findViewById(R.id.edtUsername);
        edtPhone = findViewById(R.id.edtPhone);
        tvRecoveredPassword = findViewById(R.id.tvRecoveredPassword);
        btnRecover = findViewById(R.id.btnRecover);

        userDAO = new UserDAO(getContext());

        btnRecover.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            if (username.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = userDAO.getUserByUsername(username);
            if (user != null && phone.equals(user.getPhone())) {
                tvRecoveredPassword.setText("Mật khẩu của bạn: " + user.getPassword());
                tvRecoveredPassword.setVisibility(TextView.VISIBLE);
            } else {
                tvRecoveredPassword.setVisibility(TextView.GONE);
                Toast.makeText(getContext(), "Không tìm thấy người dùng hoặc sai số điện thoại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
