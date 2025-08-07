package com.example.takeatea.fragment.User;

import android.content.Intent;
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
import com.example.takeatea.activity.OrderDetailActivity;
import com.example.takeatea.adapter.OrderAdapter;
import com.example.takeatea.dao.OrderDAO;
import com.example.takeatea.model.Order;
import com.example.takeatea.utils.SessionManager;

import java.util.List;

public class LichSuDonFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private OrderDAO orderDAO;
    private SessionManager session;

    public LichSuDonFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_order_history, container, false);

        // Ánh xạ view
        recyclerView = view.findViewById(R.id.recyclerViewOrderHistory);

        // Khởi tạo DAO và Session
        session = new SessionManager(getContext());
        orderDAO = new OrderDAO(getContext());

        // Tải dữ liệu đơn hàng
        loadOrders();

        return view;
    }

    private void loadOrders() {
        int userId = session.getUserId();
        List<Order> orderList = orderDAO.getByUserId(userId);

        adapter = new OrderAdapter(getContext(), orderList, order -> {
            // ✅ Khi bấm vào đơn → mở màn chi tiết đơn
            Intent intent = new Intent(getContext(), OrderDetailActivity.class);
            intent.putExtra("order_id", order.getId());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
