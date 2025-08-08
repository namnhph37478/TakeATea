package com.example.takeatea.fragment.Admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.takeatea.R;
import com.example.takeatea.adapter.ProductAdminAdapter;
import com.example.takeatea.dao.ProductDAO;
import com.example.takeatea.dialog.DialogSuaMon;
import com.example.takeatea.model.Product;
import com.example.takeatea.activity.ThemMonActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TrangChuFragment extends Fragment {

    private RecyclerView rcvProduct;
    private FloatingActionButton btnThem;
    private ProductAdminAdapter adapter;
    private ProductDAO productDAO;

    public TrangChuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvProduct = view.findViewById(R.id.rcvProductAdmin);
        btnThem = view.findViewById(R.id.btnThemSanPhamAdmin);
        productDAO = new ProductDAO(getContext());

        loadData();

        btnThem.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ThemMonActivity.class));
        });
    }

    private void loadData() {
        List<Product> list = productDAO.getAll();
        adapter = new ProductAdminAdapter(getContext(), list, new ProductAdminAdapter.ProductListener() {
            @Override
            public void onEdit(Product product) {
                new DialogSuaMon().show(getContext(), getActivity(), product, () -> loadData());
            }


            @Override
            public void onDelete(Product product) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Xoá sản phẩm")
                        .setMessage("Bạn có chắc muốn xoá?")
                        .setPositiveButton("Xoá", (dialog, which) -> {
                            boolean success = productDAO.delete(product.getId());
                            if (success) {
                                Toast.makeText(getContext(), "Đã xoá", Toast.LENGTH_SHORT).show();
                                loadData();
                            } else {
                                Toast.makeText(getContext(), "Xoá thất bại", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Huỷ", null)
                        .show();
            }
        });

        rcvProduct.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvProduct.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        loadData(); // reload khi thêm/xoá/sửa sản phẩm
    }
}
