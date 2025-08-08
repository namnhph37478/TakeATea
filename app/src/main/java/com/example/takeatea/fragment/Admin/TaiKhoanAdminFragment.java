package com.example.takeatea.fragment.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takeatea.R;
import com.example.takeatea.activity.LoginActivity;
import com.example.takeatea.dao.UserDAO;
import com.example.takeatea.model.User;
import com.example.takeatea.utils.SessionManager;

public class TaiKhoanAdminFragment extends Fragment {

    private TextView txtName, txtPhone, txtEmail, txtAddress;
    private ImageButton btnEdit;
    private Button btnManageCustomer, btnLogout;
    private UserDAO userDAO;
    private SessionManager sessionManager;
    private User currentAdmin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tai_khoan_admin, container, false);

        // Ánh xạ view
        txtName = view.findViewById(R.id.txtName);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtAddress = view.findViewById(R.id.txtAddress);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnManageCustomer = view.findViewById(R.id.btnManageCustomer);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Khởi tạo DAO + session
        userDAO = new UserDAO(getContext());
        sessionManager = new SessionManager(getContext());

        // Lấy thông tin admin hiện tại
        int adminId = sessionManager.getUserId();
        currentAdmin = userDAO.getUserById(adminId);

        if (currentAdmin != null) {
            showAdminInfo();
        }

        // Sự kiện sửa thông tin
        btnEdit.setOnClickListener(v -> showEditDialog());

        // Sự kiện quản lý khách hàng
        btnManageCustomer.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_admin, new KhachHangFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Sự kiện đăng xuất
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        sessionManager.logout();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });

        return view;
    }

    private void showAdminInfo() {
        txtName.setText(currentAdmin.getFullname());
        txtPhone.setText(currentAdmin.getPhone());
        txtEmail.setText(currentAdmin.getEmail());
        txtAddress.setText(currentAdmin.getAddress());
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_admin_info, null);
        builder.setView(dialogView);

        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtPhone = dialogView.findViewById(R.id.edtPhone);
        EditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        EditText edtAddress = dialogView.findViewById(R.id.edtAddress);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Set dữ liệu hiện tại
        txtName.setText(currentAdmin.getFullname());
        edtPhone.setText(currentAdmin.getPhone());
        edtEmail.setText(currentAdmin.getEmail());
        edtAddress.setText(currentAdmin.getAddress());

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật thông tin
            currentAdmin.setFullname(name);
            currentAdmin.setPhone(phone);
            currentAdmin.setEmail(email);
            currentAdmin.setAddress(address);

            if (userDAO.updateUser(currentAdmin)) {
                Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                showAdminInfo();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
