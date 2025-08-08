package com.example.takeatea.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.takeatea.R;
import com.example.takeatea.dao.CartDAO;
import com.example.takeatea.dao.ProductDAO;
import com.example.takeatea.model.Cart;
import com.example.takeatea.model.Product;
import com.example.takeatea.utils.FormatUtils;
import com.example.takeatea.utils.SessionManager;

import java.io.InputStream;

public class UserProductDetailActivity extends AppCompatActivity {

    private ImageView imgProduct;
    private TextView tvName, tvPrice, tvQty;
    private EditText edtQuantity;
    private Button btnAddToCart;
    private ImageButton btnBack;

    private ProductDAO productDAO;
    private CartDAO cartDAO;
    private SessionManager session;

    private int productId;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product_detail);

        // Ánh xạ view
        imgProduct   = findViewById(R.id.imgProduct);
        tvName       = findViewById(R.id.tvProductName);
        tvPrice      = findViewById(R.id.tvProductPrice);
        tvQty        = findViewById(R.id.tvProductQuantity);
        edtQuantity  = findViewById(R.id.edtQuantity);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBack      = findViewById(R.id.btnBack);

        // DAO & Session
        productDAO = new ProductDAO(this);
        cartDAO    = new CartDAO(this);
        session    = new SessionManager(this);

        // Lấy productId từ Intent
        productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy dữ liệu sản phẩm
        currentProduct = productDAO.getById(productId);
        if (currentProduct == null) {
            Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị thông tin sản phẩm
        tvName.setText(currentProduct.getName());
        tvPrice.setText("Giá: " + FormatUtils.formatCurrency(currentProduct.getPrice()));
        tvQty.setText("Kho: " + currentProduct.getQuantity());

        // Hiển thị ảnh
        loadImage(imgProduct, currentProduct.getImage());

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút thêm vào giỏ hàng
        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void addToCart() {
        String quantityStr = edtQuantity.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity <= 0) {
            Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity > currentProduct.getQuantity()) {
            Toast.makeText(this, "Không đủ hàng tồn", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = session.getUserId();
        boolean success = cartDAO.insertOrUpdate(new Cart(userId, productId, quantity));
        if (success) {
            Toast.makeText(this, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Hiển thị ảnh theo đường dẫn:
     * - Nếu là URI (content:// hoặc file://) → đọc stream
     * - Nếu là tên file drawable → tìm resource
     * - Nếu rỗng hoặc lỗi → dùng ảnh mặc định
     */
    private void loadImage(ImageView imageView, String imagePath) {
        int placeholder = R.drawable.ic_tea_logo;
        try {
            if (imagePath == null || imagePath.trim().isEmpty()) {
                imageView.setImageResource(placeholder);
                return;
            }

            String path = imagePath.trim();

            // 1) Ảnh từ máy (URI)
            if (path.startsWith("content://") || path.startsWith("file://")) {
                Uri uri = Uri.parse(path);
                try (InputStream is = getContentResolver().openInputStream(uri)) {
                    if (is != null) {
                        Bitmap bm = BitmapFactory.decodeStream(is);
                        imageView.setImageBitmap(bm);
                        return;
                    }
                }
                imageView.setImageResource(placeholder);
                return;
            }

            // 2) Ảnh trong drawable
            String name = path.replace(".png", "")
                    .replace(".jpg", "")
                    .replace(".jpeg", "")
                    .replace(".webp", "")
                    .replace("-", "_");
            int resId = getResources().getIdentifier(name, "drawable", getPackageName());
            if (resId != 0) {
                imageView.setImageResource(resId);
            } else {
                imageView.setImageResource(placeholder);
            }

        } catch (Exception e) {
            imageView.setImageResource(placeholder);
        }
    }
}
