package com.example.takeatea.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.adapter.OrderDetailAdapter;
import com.example.takeatea.dao.OrderDAO;
import com.example.takeatea.dao.OrderDetailDAO;
import com.example.takeatea.model.Order;
import com.example.takeatea.model.OrderDetail;
import com.example.takeatea.dialog.ReviewDialog; // Đảm bảo bạn có dialog này
import com.example.takeatea.utils.FormatUtils;

import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvShopName, tvOrderDate, tvOrderTotal;
    private RecyclerView recyclerView;
    private Button btnDanhGia;

    private int orderId;
    private Order order;
    private List<OrderDetail> orderDetails;

    private OrderDetailDAO orderDetailDAO;
    private OrderDAO orderDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Ánh xạ
        tvShopName = findViewById(R.id.tvShopName);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        recyclerView = findViewById(R.id.recyclerViewOrderDetail);
        btnDanhGia = findViewById(R.id.btnDanhGia);

        // DAO
        orderDetailDAO = new OrderDetailDAO(this);
        orderDAO = new OrderDAO(this);

        // Lấy order_id từ Intent
        orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId == -1) {
            finish();
            return;
        }

        // Lấy đơn hàng
        order = orderDAO.getById(orderId);
        if (order == null) {
            finish();
            return;
        }

        // Hiển thị thông tin đơn hàng
        tvShopName.setText("Trà Sữa TakeATea");
        tvOrderDate.setText("Ngày: " + order.getOrderDate());
        tvOrderTotal.setText("Tổng tiền: " + FormatUtils.formatCurrency(order.getTotalAmount()));

        // Load danh sách sản phẩm trong đơn
        orderDetails = orderDetailDAO.getByOrderId(orderId);
        OrderDetailAdapter adapter = new OrderDetailAdapter(this, orderDetails);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Xử lý nút đánh giá sản phẩm
        btnDanhGia.setOnClickListener(v -> {
            for (OrderDetail detail : orderDetails) {
                ReviewDialog dialog = new ReviewDialog(this, detail.getProductId(), order.getUserId());
                dialog.show();
            }
        });
    }
}
