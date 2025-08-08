package com.example.takeatea.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.dao.ProductDAO;
import com.example.takeatea.dao.PromotionDAO;
import com.example.takeatea.dao.PromotionProductDAO;
import com.example.takeatea.dialog.SuaKhuyenMaiDialog;
import com.example.takeatea.model.Product;
import com.example.takeatea.model.Promotion;

import java.util.ArrayList;

/**
 * Hiển thị danh sách khuyến mãi với thông tin:
 * - Ảnh & Tên món áp dụng
 * - Mã khuyến mãi & % giảm
 * - Nút Sửa / Xoá
 */
public class KhuyenMaiAdapter extends RecyclerView.Adapter<KhuyenMaiAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Promotion> list;
    private final PromotionDAO promotionDAO;
    private final PromotionProductDAO promotionProductDAO;
    private final ProductDAO productDAO;

    public KhuyenMaiAdapter(Context context, ArrayList<Promotion> list) {
        this.context = context;
        this.list = list;
        this.promotionDAO = new PromotionDAO(context);
        this.promotionProductDAO = new PromotionProductDAO(context);
        this.productDAO = new ProductDAO(context);
    }

    /** Cập nhật toàn bộ danh sách và render lại */
    public void refresh(ArrayList<Promotion> newData) {
        list.clear();
        list.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KhuyenMaiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_khuyen_mai, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KhuyenMaiAdapter.ViewHolder holder, int position) {
        Promotion km = list.get(position);

        // Lấy productId được liên kết với promotion hiện tại (giả sử 1 KM áp dụng 1 món)
        ArrayList<Integer> productIds = promotionProductDAO.getProductIdsByPromotionId(km.getId());

        holder.tvMaKhuyenMai.setText("Mã KM: " + km.getCode());
        holder.tvGiamGia.setText("Giảm " + km.getDiscountPercent() + "%");

        if (!productIds.isEmpty()) {
            int productId = productIds.get(0);
            Product product = productDAO.getById(productId);

            if (product != null) {
                holder.tvTenMon.setText(product.getName());

                // Load ảnh từ tên file trong drawable (ví dụ "image_trasua1.png")
                String img = product.getImage();
                if (img != null && !img.trim().isEmpty()) {
                    try {
                        String cleanName = img.replace(".png", "")
                                .replace(".jpg", "")
                                .replace(".jpeg", "")
                                .replace(".webp", "")
                                .trim();
                        int resId = context.getResources().getIdentifier(cleanName, "drawable", context.getPackageName());
                        if (resId != 0) {
                            holder.imgMonAn.setImageResource(resId);
                        } else {
                            holder.imgMonAn.setImageResource(R.drawable.ic_order); // fallback
                        }
                    } catch (Exception e) {
                        holder.imgMonAn.setImageResource(R.drawable.ic_order);
                    }
                } else {
                    holder.imgMonAn.setImageResource(R.drawable.ic_order);
                }

            } else {
                holder.tvTenMon.setText("Món đã xoá/không tồn tại");
                holder.imgMonAn.setImageResource(R.drawable.ic_order);
            }
        } else {
            // Không có liên kết món -> hiển thị placeholder
            holder.tvTenMon.setText("Chưa liên kết món");
            holder.imgMonAn.setImageResource(R.drawable.ic_order);
        }

        // Sửa khuyến mãi
        holder.btnEdit.setOnClickListener(v -> {
            SuaKhuyenMaiDialog dialog = new SuaKhuyenMaiDialog(context, km);
            dialog.setOnDismissListener(d -> {
                // Sau khi đóng dialog, đọc lại đối tượng từ DB để cập nhật view
                Promotion updated = promotionDAO.getById(km.getId());
                if (updated != null) {
                    list.set(holder.getAdapterPosition(), updated);
                    notifyItemChanged(holder.getAdapterPosition());
                } else {
                    // Có thể đã bị xoá trong dialog
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        list.remove(pos);
                        notifyItemRemoved(pos);
                    }
                }
            });
            dialog.show();
        });

        // Xoá khuyến mãi
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xoá khuyến mãi")
                    .setMessage("Bạn có chắc muốn xoá khuyến mãi này?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        // Xoá liên kết trước
                        promotionProductDAO.deleteByPromotionId(km.getId());
                        // Xoá promotion
                        int del = promotionDAO.delete(km.getId());
                        if (del > 0) {
                            int pos = holder.getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION) {
                                list.remove(pos);
                                notifyItemRemoved(pos);
                            } else {
                                notifyDataSetChanged();
                            }
                            Toast.makeText(context, "Đã xoá", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMonAn;
        TextView tvTenMon, tvMaKhuyenMai, tvGiamGia;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMonAn = itemView.findViewById(R.id.imgMonAn);
            tvTenMon = itemView.findViewById(R.id.tvTenMon);
            tvMaKhuyenMai = itemView.findViewById(R.id.tvMaKhuyenMai);
            tvGiamGia = itemView.findViewById(R.id.tvGiamGia);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
