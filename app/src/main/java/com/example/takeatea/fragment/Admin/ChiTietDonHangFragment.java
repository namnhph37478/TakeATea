package com.example.takeatea.fragment.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.dao.OrderDAO;
import com.example.takeatea.dao.OrderDetailDAO;
import com.example.takeatea.dao.ProductDAO;
import com.example.takeatea.dao.UserDAO;
import com.example.takeatea.model.Order;
import com.example.takeatea.model.OrderDetail;
import com.example.takeatea.model.Product;
import com.example.takeatea.model.User;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChiTietDonHangFragment extends Fragment {

    private int orderId;
    private TextView tvOrderCode, tvCustomer, tvDate, tvTotalAmount;
    private RecyclerView rcvOrderDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_chi_tiet_don_hang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        orderId = getArguments() != null ? getArguments().getInt("order_id", -1) : -1;

        ImageButton btnBack = v.findViewById(R.id.btnBack);
        tvOrderCode = v.findViewById(R.id.tvOrderCode);
        tvCustomer = v.findViewById(R.id.tvCustomer);
        tvDate = v.findViewById(R.id.tvDate);
        tvTotalAmount = v.findViewById(R.id.tvTotalAmount);
        rcvOrderDetails = v.findViewById(R.id.rcvOrderDetails);
        rcvOrderDetails.setLayoutManager(new LinearLayoutManager(getContext()));

        btnBack.setOnClickListener(view -> requireActivity().onBackPressed());



        OrderDAO orderDAO = new OrderDAO(getContext());
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO(getContext());
        UserDAO userDAO = new UserDAO(getContext());
        ProductDAO productDAO = new ProductDAO(getContext());

        Order order = orderDAO.getById(orderId);
        if (order == null) return;

        User user = userDAO.getUserById(order.getUserId());
        String customerName = (user != null) ? user.getFullname() : "Khách hàng";

        tvOrderCode.setText("Mã đơn: #" + order.getId());
        tvCustomer.setText("Khách: " + customerName);
        tvDate.setText("Ngày: " + (order.getOrderDate() != null ? order.getOrderDate() : ""));

        List<OrderDetail> details = orderDetailDAO.getByOrderId(orderId);
        List<OrderLineItem> items = new ArrayList<>();
        double total = 0;

        for (OrderDetail d : details) {
            Product p = productDAO.getById(d.getProductId());
            String productName = (p != null) ? p.getName() : "Sản phẩm";

            double priceEach = d.getPrice();
            int qty = d.getQuantity();
            total += priceEach * qty;

            items.add(new OrderLineItem(productName, qty, priceEach));
        }

        rcvOrderDetails.setAdapter(new OrderDetailAdapter(items));

        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalAmount.setText(vn.format(total));
    }

    static class OrderLineItem {
        final String productName;
        final int quantity;
        final double unitPrice;

        OrderLineItem(String productName, int quantity, double unitPrice) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        String priceText() {
            return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(unitPrice);
        }
    }

    static class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.VH> {
        private final List<OrderLineItem> data;

        OrderDetailAdapter(List<OrderLineItem> data) {
            this.data = data != null ? data : new ArrayList<>();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chi_tiet_don, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            OrderLineItem it = data.get(position);
            h.tvProductName.setText(it.productName);
            h.tvLinePrice.setText(it.priceText());
            h.tvQuantity.setText("x" + it.quantity);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            ImageView imgProduct;
            TextView tvProductName, tvLinePrice, tvQuantity;

            VH(View itemView) {
                super(itemView);
                imgProduct = itemView.findViewById(R.id.imgProduct);
                tvProductName = itemView.findViewById(R.id.tvProductName);
                tvLinePrice = itemView.findViewById(R.id.tvLinePrice);
                tvQuantity = itemView.findViewById(R.id.tvQuantity);
            }
        }
    }
}
