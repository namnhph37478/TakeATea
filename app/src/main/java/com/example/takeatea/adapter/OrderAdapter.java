package com.example.takeatea.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.model.Order;
import com.example.takeatea.utils.FormatUtils;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final Context context;
    private List<Order> orderList;

    public interface OnItemClickListener {
        void onClick(Order order);
    }

    private final OnItemClickListener listener;

    public OrderAdapter(Context context, List<Order> orderList, OnItemClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    public void setData(List<Order> list) {
        this.orderList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (order == null) return;

        holder.tvOrderName.setText("Đơn hàng #" + order.getId());
        holder.tvOrderPrice.setText("Tổng tiền: " + FormatUtils.formatCurrency(order.getTotalAmount()));
        holder.tvOrderQuantity.setText("Ngày: " + order.getOrderDate());

        // Set trạng thái icon (có thể thay đổi theo status)
        String status = order.getStatus();
        if ("Hoàn tất".equals(status)) {
            holder.imgStatus.setImageResource(R.drawable.ic_cart); // icon đã hoàn thành
        } else if ("Đang giao".equals(status)) {
            holder.imgStatus.setImageResource(R.drawable.ic_home); // icon đang giao
        } else {
            holder.imgStatus.setImageResource(R.drawable.ic_stats); // icon mặc định
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderName, tvOrderPrice, tvOrderQuantity;
        ImageView imgStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderName = itemView.findViewById(R.id.tvOrderName);
            tvOrderPrice = itemView.findViewById(R.id.tvOrderPrice);
            tvOrderQuantity = itemView.findViewById(R.id.tvOrderQuantity);
            imgStatus = itemView.findViewById(R.id.imgOrderStatus);
        }
    }
}
