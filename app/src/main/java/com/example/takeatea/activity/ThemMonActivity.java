package com.example.takeatea.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.takeatea.R;
import com.example.takeatea.dao.ProductDAO;
import com.example.takeatea.model.Product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ThemMonActivity extends AppCompatActivity {

    private static final int REQ_PICK_IMAGE = 1001;
    private static final int REQ_PERMISSION_33 = 2001;   // READ_MEDIA_IMAGES
    private static final int REQ_PERMISSION_OLD = 2002;  // READ_EXTERNAL_STORAGE

    private EditText edtTenMon, edtGiaMon, edtSoLuong;
    private ImageView imgMon;
    private TextView tvThemHinhMon;
    private Button btnThemMon;

    private Uri selectedImageUri = null;      // URI file nội bộ sau khi copy
    private Product productToEdit = null;
    private ProductDAO productDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_them_mon);

        edtTenMon     = findViewById(R.id.edtTenMon);
        edtGiaMon     = findViewById(R.id.edtGiaMon);
        edtSoLuong    = findViewById(R.id.edtSoLuong);
        imgMon        = findViewById(R.id.imgMon);
        tvThemHinhMon = findViewById(R.id.tvThemHinhMon);
        btnThemMon    = findViewById(R.id.btnThemMon);

        productDAO = new ProductDAO(this);

        tvThemHinhMon.setOnClickListener(v -> pickImageWithCheck());
        imgMon.setOnClickListener(v -> pickImageWithCheck());

        if (getIntent().hasExtra("product")) {
            productToEdit = (Product) getIntent().getSerializableExtra("product");
            bindDataForEdit();
        }

        btnThemMon.setOnClickListener(v -> {
            if (!validate()) return;
            if (productToEdit == null) insertProduct();
            else updateProduct();
        });
    }

    // ====== chọn ảnh + quyền ======
    private void pickImageWithCheck() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQ_PERMISSION_33);
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQ_PERMISSION_OLD);
                return;
            }
        }
        openImagePicker();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // đủ để đọc tạm thời
        startActivityForResult(intent, REQ_PICK_IMAGE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == REQ_PERMISSION_33 || requestCode == REQ_PERMISSION_OLD)
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else if (requestCode == REQ_PERMISSION_33 || requestCode == REQ_PERMISSION_OLD) {
            Toast.makeText(this, "Không có quyền truy cập ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri picked = data.getData();
            if (picked != null) {
                // KHÔNG gọi takePersistableUriPermission ở đây
                Uri local = copyImageToAppStorage(picked); // đã có READ grant tạm thời → copy được
                if (local != null) {
                    selectedImageUri = local;               // file:///data/data/..../images/xxx.jpg
                    imgMon.setImageURI(local);
                } else {
                    Toast.makeText(this, "Không thể lấy ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }




    private Uri copyImageToAppStorage(Uri sourceUri) {
        try (InputStream in = getContentResolver().openInputStream(sourceUri)) {
            if (in == null) return null;

            File dir = new File(getFilesDir(), "images");
            if (!dir.exists()) dir.mkdirs();

            String filename = "img_" + System.currentTimeMillis() + ".jpg";
            File outFile = new File(dir, filename);

            try (OutputStream out = new FileOutputStream(outFile)) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            }
            return Uri.fromFile(outFile); // file:///data/data/<pkg>/files/images/xxx.jpg
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // =================================

    private void bindDataForEdit() {
        edtTenMon.setText(productToEdit.getName());
        edtGiaMon.setText(String.valueOf(productToEdit.getPrice()));
        edtSoLuong.setText(String.valueOf(productToEdit.getQuantity()));
        if (productToEdit.getImage() != null && !productToEdit.getImage().isEmpty()) {
            selectedImageUri = Uri.parse(productToEdit.getImage());
            imgMon.setImageURI(selectedImageUri);
        }
        btnThemMon.setText("Cập nhật");
    }

    private boolean validate() {
        String ten = edtTenMon.getText().toString().trim();
        String gia = edtGiaMon.getText().toString().trim();
        String sl  = edtSoLuong.getText().toString().trim();

        if (ten.isEmpty() || gia.isEmpty() || sl.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ Tên, Giá, Số lượng", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Double.parseDouble(gia);
            Integer.parseInt(sl);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá/Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void insertProduct() {
        String name = edtTenMon.getText().toString().trim();
        double price = Double.parseDouble(edtGiaMon.getText().toString().trim());
        int quantity = Integer.parseInt(edtSoLuong.getText().toString().trim());
        String image = selectedImageUri != null ? selectedImageUri.toString() : "";

        String description = "";
        int categoryId = 1;
        int status = 1;
        float rating = 0f;

        Product p = new Product(name, price, description, image, categoryId, quantity, status, rating);
        boolean ok = productDAO.insert(p);
        if (ok) {
            Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProduct() {
        productToEdit.setName(edtTenMon.getText().toString().trim());
        productToEdit.setPrice(Double.parseDouble(edtGiaMon.getText().toString().trim()));
        productToEdit.setQuantity(Integer.parseInt(edtSoLuong.getText().toString().trim()));
        if (selectedImageUri != null) {
            productToEdit.setImage(selectedImageUri.toString()); // chỉ đổi ảnh khi chọn mới
        }

        boolean ok = productDAO.update(productToEdit);
        if (ok) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
