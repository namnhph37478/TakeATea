package com.example.takeatea.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.dao.ProductDAO;
import com.example.takeatea.model.OrderDetail;
import com.example.takeatea.model.Product;
import com.example.takeatea.utils.FormatUtils;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private final Context context;
    private final List<OrderDetail> orderDetailList;
    private final ProductDAO productDAO;

    public OrderDetailAdapter(Context context, List<OrderDetail> orderDetailList) {
        this.context = context;
        this.orderDetailList = orderDetailList;
        this.productDAO = new ProductDAO(context);
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail detail = orderDetailList.get(position);
        if (detail == null) return;

        Product product = productDAO.getById(detail.getProductId());
        if (product == null) return;

        holder.tvTenMon.setText(product.getName());
        holder.tvSoLuong.setText("Số lượng: " + detail.getQuantity());
        holder.tvGia.setText("Giá: " + FormatUtils.formatCurrency(detail.getPrice()));
    }

    @Override
    public int getItemCount() {
        return (orderDetailList != null) ? orderDetailList.size() : 0;
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenMon, tvSoLuong, tvGia;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenMon = itemView.findViewById(R.id.tvTenMon);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            tvGia = itemView.findViewById(R.id.tvGia);
        }
    }
}
