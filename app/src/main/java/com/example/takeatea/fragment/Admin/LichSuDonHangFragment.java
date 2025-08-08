package com.example.takeatea.fragment.Admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.takeatea.R;
import com.example.takeatea.dao.OrderDAO;
import com.example.takeatea.dao.OrderDetailDAO;
import com.example.takeatea.dao.UserDAO;
import com.example.takeatea.model.Order;
import com.example.takeatea.model.OrderDetail;
import com.example.takeatea.model.User;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LichSuDonHangFragment extends Fragment {

    private RecyclerView rcvLichSuDonHang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_lich_su_don_hang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvLichSuDonHang = view.findViewById(R.id.rcvLichSuDonHang);
        rcvLichSuDonHang.setLayoutManager(new LinearLayoutManager(getContext()));

        OrderDAO orderDAO = new OrderDAO(getContext());
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO(getContext());
        UserDAO userDAO = new UserDAO(getContext());

        List<AdminOrderItem> items = new ArrayList<>();

        List<Order> orders = orderDAO.getAll(); // lấy tất cả đơn hàng
        for (Order o : orders) {
            User u = userDAO.getUserById(o.getUserId());
            String customerName = (u != null) ? u.getFullname() : "Khách hàng";

            List<OrderDetail> details = orderDetailDAO.getByOrderId(o.getId());
            int totalQty = 0;
            double totalPrice = 0;
            for (OrderDetail d : details) {
                totalQty += d.getQuantity();
                totalPrice += d.getQuantity() * d.getPrice();
            }

            items.add(new AdminOrderItem(o.getId(), customerName, o.getOrderDate(), totalQty, totalPrice));
        }

        LichSuDonHangAdapter adapter = new LichSuDonHangAdapter(items, item -> {
            // Chuyển sang fragment chi tiết đơn
            Bundle args = new Bundle();
            args.putInt("order_id", item.orderId);
            ChiTietDonHangFragment frag = new ChiTietDonHangFragment();
            frag.setArguments(args);

            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_admin, frag);
            ft.addToBackStack(null);
            ft.commit();
        });

        rcvLichSuDonHang.setAdapter(adapter);
    }

    static class AdminOrderItem {
        final int orderId;
        final String customerName;
        final String date;
        final int totalQuantity;
        final double totalPrice;

        AdminOrderItem(int orderId, String customerName, String date, int totalQuantity, double totalPrice) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.date = date;
            this.totalQuantity = totalQuantity;
            this.totalPrice = totalPrice;
        }

        String formatCurrency() {
            return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(totalPrice);
        }
    }

    static class LichSuDonHangAdapter extends RecyclerView.Adapter<LichSuDonHangAdapter.VH> {

        interface OnItemClick { void onClick(AdminOrderItem item); }

        private final List<AdminOrderItem> data;
        private final OnItemClick listener;

        LichSuDonHangAdapter(List<AdminOrderItem> data, OnItemClick listener) {
            this.data = data;
            this.listener = listener;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lich_su_don, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            AdminOrderItem item = data.get(position);
            h.tvTenKhachHang.setText(item.customerName + " • Mã đơn #" + item.orderId);
            h.tvDonGia.setText("Tổng tiền: " + item.formatCurrency());
            h.tvSoLuong.setText("Số lượng: " + item.totalQuantity);

            h.itemView.setOnClickListener(v -> listener.onClick(item));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvTenKhachHang, tvDonGia, tvSoLuong;
            VH(View itemView) {
                super(itemView);
                tvTenKhachHang = itemView.findViewById(R.id.tvTenKhachHang);
                tvDonGia = itemView.findViewById(R.id.tvDonGia);
                tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            }
        }
    }
}
