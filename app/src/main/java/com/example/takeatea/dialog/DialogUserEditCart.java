package com.example.takeatea.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.example.takeatea.R;
import com.example.takeatea.dao.CartDAO;
import com.example.takeatea.dao.ProductDAO;
import com.example.takeatea.model.Cart;
import com.example.takeatea.model.Product;

public class DialogUserEditCart {


    public interface OnSaved { void onSaved(); }

    public static void show(Context context, Cart cart, OnSaved cb) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_user_edit_cart, null);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();

        ImageView btnBack = view.findViewById(R.id.btnBack);
        EditText edtSoLuong = view.findViewById(R.id.edtSoLuong);
        Button btnLuu = view.findViewById(R.id.btnLuu);

        edtSoLuong.setText(String.valueOf(cart.getQuantity()));

        CartDAO cartDAO = new CartDAO(context);
        ProductDAO productDAO = new ProductDAO(context);

        btnBack.setOnClickListener(v -> dialog.dismiss());

        btnLuu.setOnClickListener(v -> {
            String qtyStr = edtSoLuong.getText().toString().trim();
            if (qtyStr.isEmpty()) {
                edtSoLuong.setError("Nhập số lượng");
                return;
            }
            int qty;
            try { qty = Integer.parseInt(qtyStr); } catch (Exception e) { qty = -1; }
            if (qty <= 0) {
                edtSoLuong.setError("Số lượng phải > 0");
                return;
            }

            // (khuyến nghị) không cho vượt tồn kho
            Product p = productDAO.getById(cart.getProductId());
            if (p != null && qty > p.getQuantity()) {
                edtSoLuong.setError("Vượt tồn kho (" + p.getQuantity() + ")");
                return;
            }

            boolean ok = cartDAO.updateQuantity(cart.getUserId(), cart.getProductId(), qty);
            if (ok) {
                Toast.makeText(context, "Đã cập nhật số lượng", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                if (cb != null) cb.onSaved();
            } else {
                Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
