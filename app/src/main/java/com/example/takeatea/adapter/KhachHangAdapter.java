package com.example.takeatea.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.model.User;

import java.util.ArrayList;

public class KhachHangAdapter extends RecyclerView.Adapter<KhachHangAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(User user);
        void onDeleteClick(User user);
    }

    private Context context;
    private ArrayList<User> list;
    private OnItemClickListener listener;

    public KhachHangAdapter(Context context, ArrayList<User> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_khach_hang, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);
        holder.tvTenKhachHang.setText(user.getFullname());
        holder.tvSDTKhach.setText("SĐT: " + user.getPhone());
        holder.tvDiaChiKhach.setText("Địa chỉ: " + user.getAddress());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(user));
        holder.btnDeleteKhach.setOnClickListener(v -> listener.onDeleteClick(user));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenKhachHang, tvSDTKhach, tvDiaChiKhach;
        ImageButton btnDeleteKhach;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenKhachHang = itemView.findViewById(R.id.tvTenKhachHang);
            tvSDTKhach = itemView.findViewById(R.id.tvSDTKhach);
            tvDiaChiKhach = itemView.findViewById(R.id.tvDiaChiKhach);
            btnDeleteKhach = itemView.findViewById(R.id.btnDeleteKhach);
        }
    }
}
