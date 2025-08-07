package com.example.takeatea.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.takeatea.R;
import com.example.takeatea.dao.UserDAO;
import com.example.takeatea.model.User;

public class ChangePasswordDialog {

    public static void show(Context context, User user, UserDAO userDAO, Runnable onSuccess) {
        if (user == null) {
            Toast.makeText(context, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null);
        EditText edtOld = view.findViewById(R.id.edtOldPassword);
        EditText edtNew = view.findViewById(R.id.edtNewPassword);
        EditText edtConfirm = view.findViewById(R.id.edtConfirmPassword);

        new AlertDialog.Builder(context)
                .setTitle("Đổi mật khẩu")
                .setView(view)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String oldPass = edtOld.getText().toString().trim();
                    String newPass = edtNew.getText().toString().trim();
                    String confirmPass = edtConfirm.getText().toString().trim();

                    if (!user.getPassword().equals(oldPass)) {
                        Toast.makeText(context, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!newPass.equals(confirmPass)) {
                        Toast.makeText(context, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    user.setPassword(newPass);
                    boolean success = userDAO.updateUser(user);
                    if (success) {
                        Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        if (onSuccess != null) onSuccess.run();
                    } else {
                        Toast.makeText(context, "Lỗi khi đổi mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
