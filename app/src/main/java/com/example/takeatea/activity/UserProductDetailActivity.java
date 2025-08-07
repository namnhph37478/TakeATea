package com.example.takeatea.activity;

import android.os.Bundle;
import android.view.View;
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

        // Ánh xạ
        imgProduct = findViewById(R.id.imgProduct);
        tvName = findViewById(R.id.tvProductName);
        tvPrice = findViewById(R.id.tvProductPrice);
        tvQty = findViewById(R.id.tvProductQuantity);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBack = findViewById(R.id.btnBack);

        productDAO = new ProductDAO(this);
        cartDAO = new CartDAO(this);
        session = new SessionManager(this);

        // Lấy product ID
        productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load sản phẩm
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

        // TODO: Load ảnh nếu bạn có ảnh từ link hoặc tên file

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Thêm vào giỏ hàng
        btnAddToCart.setOnClickListener(v -> {
            String quantityStr = edtQuantity.getText().toString().trim();
            if (quantityStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (quantity > currentProduct.getQuantity()) {
                Toast.makeText(this, "Không đủ hàng tồn", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = session.getUserId();
            Cart cart = new Cart(userId, productId, quantity);
            boolean success = cartDAO.insertOrUpdate(cart);

            if (success) {
                int updatedQty = currentProduct.getQuantity() - quantity;
                productDAO.updateQuantity(productId, updatedQty);

                Toast.makeText(this, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
