package com.example.takeatea.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.activity.UserProductDetailActivity;
import com.example.takeatea.model.Product;
import com.example.takeatea.utils.FormatUtils;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.productList = list;
    }

    public void setData(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = productList.get(position);
        if (p == null) return;

        holder.tvName.setText(p.getName());
        holder.tvPrice.setText("Giá: " + FormatUtils.formatCurrency(p.getPrice()));
        holder.tvQty.setText("Số lượng: " + p.getQuantity());

        View.OnClickListener openDetail = v -> {
            Intent intent = new Intent(context, UserProductDetailActivity.class);
            intent.putExtra("product_id", p.getId());
            context.startActivity(intent);
        };

        // Bấm vào ảnh giỏ hoặc toàn bộ item để mở chi tiết
        holder.imgCart.setOnClickListener(openDetail);
        holder.itemView.setOnClickListener(openDetail);
    }

    @Override
    public int getItemCount() {
        return (productList != null) ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQty;
        ImageView imgCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty = itemView.findViewById(R.id.tvQty);
            imgCart = itemView.findViewById(R.id.imgCart);
        }
    }
}
