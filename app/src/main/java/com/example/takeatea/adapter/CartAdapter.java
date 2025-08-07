package com.example.takeatea.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.dao.ProductDAO;
import com.example.takeatea.model.Cart;
import com.example.takeatea.model.Product;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface CartItemListener {
        void onEdit(Cart cart);   // Gọi khi bấm "sửa"
        void onDelete(Cart cart); // Gọi khi bấm "xóa"
    }

    private final Context context;
    private final List<Cart> cartList;
    private final CartItemListener listener;
    private final ProductDAO productDAO;

    public CartAdapter(Context context, List<Cart> cartList, CartItemListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
        this.productDAO = new ProductDAO(context);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        if (cart == null) return;

        // Lấy thông tin sản phẩm từ ID
        Product product = productDAO.getById(cart.getProductId());
        if (product == null) return;

        holder.tvName.setText(product.getName());
        holder.tvQuantity.setText("Số lượng: " + cart.getQuantity());

        // TODO: Nếu có ảnh, load vào imgCartProduct
        // holder.imgCartProduct.setImageResource(...);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(cart));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(cart));
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCartProduct;
        TextView tvName, tvQuantity;
        ImageButton btnEdit, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCartProduct = itemView.findViewById(R.id.imgCartProduct);
            tvName = itemView.findViewById(R.id.tvCartProductName);
            tvQuantity = itemView.findViewById(R.id.tvCartQuantity);
            btnEdit = itemView.findViewById(R.id.btnEditCart);
            btnDelete = itemView.findViewById(R.id.btnDeleteCart);
        }
    }
}
