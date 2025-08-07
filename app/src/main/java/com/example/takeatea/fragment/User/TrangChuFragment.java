package com.example.takeatea.fragment.User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.adapter.ProductAdapter;
import com.example.takeatea.dao.ProductDAO;
import com.example.takeatea.model.Product;

import java.util.ArrayList;
import java.util.List;

public class TrangChuFragment extends Fragment {

    private RecyclerView rcvUserProduct;
    private ProductAdapter adapter;
    private ProductDAO productDAO;
    private List<Product> productList = new ArrayList<>();

    public TrangChuFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        // Ánh xạ RecyclerView
        rcvUserProduct = view.findViewById(R.id.rcvUserProduct);

        // Khởi tạo DAO và Adapter
        productDAO = new ProductDAO(getContext());
        adapter = new ProductAdapter(getContext(), productList);

        // Cấu hình RecyclerView
        rcvUserProduct.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvUserProduct.setAdapter(adapter);

        // Load sản phẩm
        loadProducts();

        return view;
    }

    private void loadProducts() {
        productList.clear();
        productList.addAll(productDAO.getAll());
        adapter.notifyDataSetChanged();
    }
}
