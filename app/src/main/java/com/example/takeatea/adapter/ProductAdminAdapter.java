package com.example.takeatea.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.model.Product;

import java.io.File;
import java.util.List;

public class ProductAdminAdapter extends RecyclerView.Adapter<ProductAdminAdapter.ProductViewHolder> {

    public interface ProductListener {
        void onEdit(Product product);
        void onDelete(Product product);
    }

    private final Context context;
    private final List<Product> list;
    private final ProductListener listener;

    public ProductAdminAdapter(Context context, List<Product> list, ProductListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_admin, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = list.get(position);
        holder.tvName.setText(p.getName());
        holder.tvPrice.setText("Giá: " + p.getPrice() + " đ");
        holder.tvQuantity.setText("SL: " + p.getQuantity());

        bindImage(holder.imgProduct, p.getImage());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(p));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(p));
    }

    private void bindImage(ImageView iv, String image) {
        if (image == null || image.trim().isEmpty()) {
            iv.setImageResource(R.drawable.ic_tea_logo);
            return;
        }

        String s = image.trim();

        // 1) Đường dẫn file tuyệt đối
        if (s.startsWith("/")) {
            iv.setImageURI(Uri.fromFile(new File(s)));
            return;
        }

        String lower = s.toLowerCase();

        // 2) content:// hoặc file://
        if (lower.startsWith("content://") || lower.startsWith("file://")) {
            iv.setImageURI(Uri.parse(s));
            return;
        }

        // 3) Tên resource trong drawable/mipmap (image_trasua1.png / image_trasua1)
        String name = s.contains(".") ? s.substring(0, s.lastIndexOf('.')) : s;

        int resId = iv.getContext().getResources()
                .getIdentifier(name, "drawable", iv.getContext().getPackageName());
        if (resId == 0) {
            resId = iv.getContext().getResources()
                    .getIdentifier(name, "mipmap", iv.getContext().getPackageName());
        }
        if (resId != 0) {
            iv.setImageResource(resId);
        } else {
            iv.setImageResource(R.drawable.ic_tea_logo);
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvQuantity;
        ImageButton btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvProductQuantity);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
