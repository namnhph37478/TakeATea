package com.example.takeatea.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.InputStream;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductVH> {

    private final Context context;
    private final List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull @Override
    public ProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_user_product, parent, false);
        return new ProductVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductVH h, int position) {
        Product p = productList.get(position);
        if (p == null) return;

        h.tvName.setText(p.getName());
        h.tvPrice.setText("Giá: " + FormatUtils.formatCurrency(p.getPrice()));
        h.tvQty.setText("Số lượng: " + p.getQuantity());

        loadImage(h.imgProduct, p.getImage());

        // Mở chi tiết sản phẩm khi click item
        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, UserProductDetailActivity.class);
            i.putExtra("product_id", p.getId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    // -------- Helpers --------
    private void loadImage(ImageView imageView, String imagePath) {
        // Ảnh mặc định
        int placeholder = R.drawable.ic_tea_logo;

        try {
            if (imagePath == null || imagePath.trim().isEmpty()) {
                imageView.setImageResource(placeholder);
                return;
            }

            String path = imagePath.trim();

            // 1) Ảnh từ URI (admin chọn từ máy) -> content:// hoặc file://
            if (path.startsWith("content://") || path.startsWith("file://")) {
                Uri uri = Uri.parse(path);
                try (InputStream is = context.getContentResolver().openInputStream(uri)) {
                    if (is != null) {
                        Bitmap bm = BitmapFactory.decodeStream(is);
                        imageView.setImageBitmap(bm);
                        return;
                    }
                }
                imageView.setImageResource(placeholder);
                return;
            }

            // 2) Ảnh đặt theo tên file trong drawable (vd: image_trasua1.png)
            String name = path
                    .replace(".png", "")
                    .replace(".jpg", "")
                    .replace(".jpeg", "")
                    .replace(".webp", "")
                    .replace("-", "_"); // phòng tên có dấu '-'

            int resId = context.getResources()
                    .getIdentifier(name, "drawable", context.getPackageName());

            if (resId != 0) {
                imageView.setImageResource(resId);
            } else {
                imageView.setImageResource(placeholder);
            }
        } catch (Exception e) {
            imageView.setImageResource(placeholder);
        }
    }

    static class ProductVH extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvQty;
        ProductVH(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName     = itemView.findViewById(R.id.tvName);
            tvPrice    = itemView.findViewById(R.id.tvPrice);
            tvQty      = itemView.findViewById(R.id.tvQty);
        }
    }
}
