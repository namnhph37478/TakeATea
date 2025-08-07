package com.example.takeatea.fragment.User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.adapter.CartAdapter;
import com.example.takeatea.dao.CartDAO;
import com.example.takeatea.dao.OrderDAO;
import com.example.takeatea.dao.OrderDetailDAO;
import com.example.takeatea.dao.ProductDAO;
import com.example.takeatea.model.Cart;
import com.example.takeatea.model.Order;
import com.example.takeatea.model.OrderDetail;
import com.example.takeatea.model.Product;
import com.example.takeatea.utils.FormatUtils;
import com.example.takeatea.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GioHangFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnDatHang;
    private CartAdapter adapter;
    private CartDAO cartDAO;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private SessionManager session;
    private List<Cart> cartList;

    public GioHangFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_cart, container, false);

        // Ánh xạ
        recyclerView = view.findViewById(R.id.recyclerViewCart);
        btnDatHang = view.findViewById(R.id.btnUserDatHang);

        // Khởi tạo
        session = new SessionManager(getContext());
        cartDAO = new CartDAO(getContext());
        productDAO = new ProductDAO(getContext());
        orderDAO = new OrderDAO(getContext());
        orderDetailDAO = new OrderDetailDAO(getContext());

        // Load dữ liệu
        loadCart();

        // Xử lý đặt hàng
        btnDatHang.setOnClickListener(v -> datHang());

        return view;
    }

    private void loadCart() {
        int userId = session.getUserId();
        cartList = cartDAO.getCartByUserId(userId);

        adapter = new CartAdapter(getContext(), cartList, new CartAdapter.CartItemListener() {
            @Override
            public void onEdit(Cart cart) {
                Toast.makeText(getContext(), "TODO: Sửa số lượng", Toast.LENGTH_SHORT).show();
                // TODO: mở dialog sửa nếu muốn
            }

            @Override
            public void onDelete(Cart cart) {
                cartDAO.deleteItem(cart.getUserId(), cart.getProductId());
                Toast.makeText(getContext(), "Đã xóa khỏi giỏ", Toast.LENGTH_SHORT).show();
                loadCart(); // reload lại
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void datHang() {
        if (cartList == null || cartList.isEmpty()) {
            Toast.makeText(getContext(), "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = session.getUserId();
        double totalAmount = 0;

        // Tính tổng tiền
        for (Cart cart : cartList) {
            Product p = productDAO.getById(cart.getProductId());
            if (p != null) {
                totalAmount += p.getPrice() * cart.getQuantity();
            }
        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Order order = new Order(userId, date, totalAmount, "Đang xử lý");

        boolean orderCreated = orderDAO.insert(order);
        int orderId = orderDAO.getLastOrderId(); // bạn cần có hàm này

        if (orderCreated && orderId != -1) {
            // Thêm từng sản phẩm vào OrderDetail
            for (Cart cart : cartList) {
                Product p = productDAO.getById(cart.getProductId());
                if (p != null) {
                    // Trừ tồn kho
                    int newQty = p.getQuantity() - cart.getQuantity();
                    productDAO.updateQuantity(p.getId(), newQty);

                    // Thêm vào bảng chi tiết đơn
                    OrderDetail detail = new OrderDetail(orderId, p.getId(), cart.getQuantity(), p.getPrice());
                    orderDetailDAO.insert(detail);
                }
            }

            // Xóa giỏ
            cartDAO.clearCart(userId);
            Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
            loadCart(); // reload lại sau khi xóa
        } else {
            Toast.makeText(getContext(), "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}
