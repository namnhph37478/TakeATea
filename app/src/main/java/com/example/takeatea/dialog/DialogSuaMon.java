package com.example.takeatea.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;

import com.example.takeatea.R;
import com.example.takeatea.dao.ProductDAO;
import com.example.takeatea.model.Product;

public class DialogSuaMon {

    private static final int PICK_IMAGE_REQUEST = 202;
    private Uri selectedImageUri = null;
    private ImageView imgMonGlobal;

    public interface OnProductUpdatedListener {
        void onUpdated();
    }

    public void show(Context context, Activity activity, Product product, OnProductUpdatedListener callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_admin_sua_mon, null);
        builder.setView(view);

        EditText edtTenMon = view.findViewById(R.id.edtTenMon);
        EditText edtGiaMon = view.findViewById(R.id.edtGiaMon);
        EditText edtSoLuong = view.findViewById(R.id.edtSoLuong);
        imgMonGlobal = view.findViewById(R.id.imgMon);
        TextView tvThemHinhMon = view.findViewById(R.id.tvThemHinhMon);
        Button btnLuu = view.findViewById(R.id.btnLuu);

        edtTenMon.setText(product.getName());
        edtGiaMon.setText(String.valueOf(product.getPrice()));
        edtSoLuong.setText(String.valueOf(product.getQuantity()));

        if (product.getImage() != null && !product.getImage().isEmpty()) {
            selectedImageUri = Uri.parse(product.getImage());
            imgMonGlobal.setImageURI(selectedImageUri);
        }

        View.OnClickListener imagePicker = v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activity.startActivityForResult(intent, PICK_IMAGE_REQUEST);
        };
        imgMonGlobal.setOnClickListener(imagePicker);
        tvThemHinhMon.setOnClickListener(imagePicker);

        btnLuu.setOnClickListener(v -> {
            String ten = edtTenMon.getText().toString().trim();
            String giaStr = edtGiaMon.getText().toString().trim();
            String slStr = edtSoLuong.getText().toString().trim();

            if (ten.isEmpty() || giaStr.isEmpty() || slStr.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            double gia = Double.parseDouble(giaStr);
            int sl = Integer.parseInt(slStr);

            product.setName(ten);
            product.setPrice(gia);
            product.setQuantity(sl);
            if (selectedImageUri != null) {
                product.setImage(selectedImageUri.toString());
            }

            boolean updated = new ProductDAO(context).update(product);
            if (updated) {
                Toast.makeText(context, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                callback.onUpdated();
            } else {
                Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    public void handleImageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (imgMonGlobal != null && selectedImageUri != null) {
                imgMonGlobal.setImageURI(selectedImageUri);
            }
        }
    }
}
