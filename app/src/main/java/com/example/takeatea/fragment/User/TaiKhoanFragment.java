package com.example.takeatea.fragment.User;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.takeatea.R;
import com.example.takeatea.activity.LoginActivity;
import com.example.takeatea.dao.UserDAO;
import com.example.takeatea.dialog.ChangePasswordDialog;
import com.example.takeatea.model.User;
import com.example.takeatea.utils.SessionManager;

public class TaiKhoanFragment extends Fragment {

    private TextView txtName, txtPhone, txtEmail, txtAddress;
    private ImageButton btnEditProfile;
    private Button btnChangePassword, btnLogout;

    private SessionManager sessionManager;
    private UserDAO userDAO;
    private User currentUser;

    public TaiKhoanFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_taikhoan, container, false);

        // Ánh xạ
        txtName = view.findViewById(R.id.txtName);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtAddress = view.findViewById(R.id.txtAddress);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        sessionManager = new SessionManager(getContext());
        userDAO = new UserDAO(getContext());

        loadUserInfo();

        btnEditProfile.setOnClickListener(v -> showEditDialog());
        btnChangePassword.setOnClickListener(v -> ChangePasswordDialog.show(getContext(), currentUser, userDAO, this::loadUserInfo));
        btnLogout.setOnClickListener(v -> confirmLogout());

        return view;
    }

    private void loadUserInfo() {
        int userId = sessionManager.getUserId();
        currentUser = userDAO.getUserById(userId);

        if (currentUser != null) {
            txtName.setText(currentUser.getFullname());
            txtPhone.setText(currentUser.getPhone());
            txtEmail.setText(currentUser.getEmail());
            txtAddress.setText(currentUser.getAddress());
        } else {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDialog() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "Lỗi tải người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText edtName = dialogView.findViewById(R.id.edtEditName);
        EditText edtPhone = dialogView.findViewById(R.id.edtEditPhone);
        EditText edtEmail = dialogView.findViewById(R.id.edtEditEmail);
        EditText edtAddress = dialogView.findViewById(R.id.edtEditAddress);

        // Set dữ liệu cũ
        edtName.setText(currentUser.getFullname());
        edtPhone.setText(currentUser.getPhone());
        edtEmail.setText(currentUser.getEmail());
        edtAddress.setText(currentUser.getAddress());

        new AlertDialog.Builder(getContext())
                .setTitle("Chỉnh sửa thông tin")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String name = edtName.getText().toString().trim();
                    String phone = edtPhone.getText().toString().trim();
                    String email = edtEmail.getText().toString().trim();
                    String address = edtAddress.getText().toString().trim();

                    if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    currentUser.setFullname(name);
                    currentUser.setPhone(phone);
                    currentUser.setEmail(email);
                    currentUser.setAddress(address);

                    boolean success = userDAO.updateUser(currentUser);
                    if (success) {
                        loadUserInfo();
                        Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(getContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
