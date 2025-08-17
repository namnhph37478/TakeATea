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
    private AlertDialog dialog;
    private Activity hostActivity; // lưu Activity để xin/persist permission

    public interface OnProductUpdatedListener {
        void onUpdated();
    }

    public void show(Context context, Activity activity, Product product, OnProductUpdatedListener callback) {
        this.hostActivity = activity;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_admin_sua_mon, null);
        builder.setView(view);

        EditText edtTenMon = view.findViewById(R.id.edtTenMon);
        EditText edtGiaMon = view.findViewById(R.id.edtGiaMon);
        EditText edtSoLuong = view.findViewById(R.id.edtSoLuong);
        imgMonGlobal = view.findViewById(R.id.imgMon);
        TextView tvThemHinhMon = view.findViewById(R.id.tvThemHinhMon);
        Button btnLuu = view.findViewById(R.id.btnLuu);

        // dữ liệu ban đầu
        edtTenMon.setText(product.getName());
        edtGiaMon.setText(String.valueOf(product.getPrice()));
        edtSoLuong.setText(String.valueOf(product.getQuantity()));
        showExistingImage(context, product.getImage());

        // chọn ảnh qua SAF (ổn định hơn ACTION_PICK/Google Photos)
        View.OnClickListener imagePicker = v -> {
            if (hostActivity != null) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                hostActivity.startActivityForResult(intent, PICK_IMAGE_REQUEST);
            } else {
                Toast.makeText(context, "Không thể mở thư viện ảnh", Toast.LENGTH_SHORT).show();
            }
        };
        imgMonGlobal.setOnClickListener(imagePicker);
        tvThemHinhMon.setOnClickListener(imagePicker);

        // lưu
        btnLuu.setOnClickListener(v -> {
            String ten = edtTenMon.getText().toString().trim();
            String giaStr = edtGiaMon.getText().toString().trim();
            String slStr = edtSoLuong.getText().toString().trim();

            if (ten.isEmpty() || giaStr.isEmpty() || slStr.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            double gia;
            int sl;
            try {
                gia = Double.parseDouble(giaStr);
                sl = Integer.parseInt(slStr);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Giá/Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            product.setName(ten);
            product.setPrice(gia);
            product.setQuantity(sl);
            if (selectedImageUri != null) {
                product.setImage(selectedImageUri.toString());
            }

            boolean updated = new ProductDAO(context).update(product);
            if (updated) {
                Toast.makeText(context, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                if (callback != null) callback.onUpdated();
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
            } else {
                Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    // Giữ nguyên chữ ký cũ để không ảnh hưởng nơi gọi
    public void handleImageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedImageUri = uri;
                // Persist quyền đọc để hiển thị bền vững (kể cả app restart)
                try {
                    if (hostActivity != null) {
                        hostActivity.getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                    }
                } catch (Exception ignored) {
                    // có thể source không hỗ trợ persist; bỏ qua an toàn
                }

                if (imgMonGlobal != null) {
                    imgMonGlobal.setImageURI(selectedImageUri);
                }
            }
        }
    }

    // -------- Helpers --------
    private void showExistingImage(Context context, @Nullable String imagePath) {
        if (imgMonGlobal == null) return;

        int placeholder = R.drawable.ic_tea_logo;

        if (imagePath == null || imagePath.trim().isEmpty()) {
            imgMonGlobal.setImageResource(placeholder);
            return;
        }

        String path = imagePath.trim();

        // URI (content://, file://)
        if (path.startsWith("content://") || path.startsWith("file://")) {
            try {
                selectedImageUri = Uri.parse(path);
                imgMonGlobal.setImageURI(selectedImageUri);
            } catch (Exception e) {
                imgMonGlobal.setImageResource(placeholder);
            }
            return;
        }

        // tên drawable
        String name = path
                .replace(".png", "")
                .replace(".jpg", "")
                .replace(".jpeg", "")
                .replace(".webp", "")
                .replace("-", "_");

        int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        if (resId != 0) {
            imgMonGlobal.setImageResource(resId);
        } else {
            imgMonGlobal.setImageResource(placeholder);
        }
    }
}
