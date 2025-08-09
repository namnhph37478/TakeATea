package com.example.takeatea.fragment.User;

import android.os.Bundle;
import android.util.Log;
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
import com.example.takeatea.dialog.DialogSuaMon;
import com.example.takeatea.dialog.DialogUserEditCart;
import com.example.takeatea.model.Cart;
import com.example.takeatea.model.Order;
import com.example.takeatea.model.OrderDetail;
import com.example.takeatea.model.Product;
import com.example.takeatea.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                DialogUserEditCart.show(
                        requireContext(),
                        cart,
                        () -> loadCart()
                );
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
        btnDatHang.setEnabled(false); // tránh click 2 lần

        if (cartList == null || cartList.isEmpty()) {
            Toast.makeText(getContext(), "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
            btnDatHang.setEnabled(true);
            return;
        }

        int userId = session.getUserId();
        double totalAmount = 0;

        // Gộp lại theo productId
        Map<Integer, Integer> productQtyMap = new HashMap<>();
        for (Cart cart : cartList) {
            int pid = cart.getProductId();
            int qty = cart.getQuantity();
            productQtyMap.put(pid, productQtyMap.getOrDefault(pid, 0) + qty);

            Product p = productDAO.getById(pid);
            if (p != null) {
                totalAmount += p.getPrice() * qty;
            }
        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Order order = new Order(userId, date, totalAmount, "Đang xử lý");

        boolean orderCreated = orderDAO.insert(order);
        int orderId = orderDAO.getLastOrderId();

        if (orderCreated && orderId != -1) {
            for (Cart cart : cartList) {
                Product p = productDAO.getById(cart.getProductId());
                if (p != null) {
                    int quantityTrongKho = p.getQuantity();
                    int quantityDatHang = cart.getQuantity();

                    // Đảm bảo trừ đúng số lượng 1 lần
                    int quantityMoi = quantityTrongKho - quantityDatHang;
                    if (quantityMoi < 0) quantityMoi = 0; // Tránh âm

                    // Cập nhật tồn kho
                    boolean updated = productDAO.updateQuantity(p.getId(), quantityMoi);

                    // Ghi log kiểm tra
                    android.util.Log.d("DEBUG", "Update SL: " + p.getName() + ", old: " + quantityTrongKho + ", new: " + quantityMoi);

                    // Lưu chi tiết đơn hàng
                    OrderDetail detail = new OrderDetail(orderId, p.getId(), quantityDatHang, p.getPrice());
                    orderDetailDAO.insert(detail);
                }
            }

            // Xóa giỏ
            cartDAO.clearCart(userId);
            Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
            loadCart();
        }
        else {
            Toast.makeText(getContext(), "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
        }

        btnDatHang.setEnabled(true);
    }

}
