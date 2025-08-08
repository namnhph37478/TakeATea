package com.example.takeatea.fragment.Admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.adapter.KhachHangAdapter;
import com.example.takeatea.dao.UserDAO;
import com.example.takeatea.model.User;

import java.util.ArrayList;

public class KhachHangFragment extends Fragment {

    private RecyclerView rcvKhachHang;
    private UserDAO userDAO;
    private KhachHangAdapter adapter;
    private ArrayList<User> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_quan_ly_khach_hang, container, false);
        rcvKhachHang = v.findViewById(R.id.rcvKhachHang);
        rcvKhachHang.setLayoutManager(new LinearLayoutManager(getContext()));
        userDAO = new UserDAO(getContext());

        adapter = new KhachHangAdapter(getContext(), list, new KhachHangAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                Intent intent = new Intent(getContext(), ChiTietKhachHangActivity.class);
                intent.putExtra("userId", user.getId()); // nhớ dùng đúng key
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(User user) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn chắc chắn muốn xóa khách hàng này?")
                        .setPositiveButton("Xóa", (d, w) -> {
                            if (userDAO.deleteUser(user.getId())) {
                                Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                                loadData();
                            } else {
                                Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        rcvKhachHang.setAdapter(adapter);

        loadData();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // quay lại list sẽ cập nhật
    }

    private void loadData() {
        list.clear();
        list.addAll(userDAO.getAllUsersByRole("user"));
        adapter.notifyDataSetChanged();
    }

}
