package com.example.takeatea.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.takeatea.R;
import com.example.takeatea.dao.ProductDAO;
import com.example.takeatea.dao.PromotionDAO;
import com.example.takeatea.dao.PromotionProductDAO;
import com.example.takeatea.model.Product;
import com.example.takeatea.model.Promotion;

import java.util.ArrayList;

public class ThemKhuyenMaiActivity extends AppCompatActivity {

    private EditText edtMaKM, edtPhanTram;
    private Spinner spinnerMonAn;
    private Button btnThem;
    private PromotionDAO promotionDAO;
    private PromotionProductDAO promotionProductDAO;
    private ProductDAO productDAO;
    private ArrayList<Product> products;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_them_khuyen_mai);

        edtMaKM = findViewById(R.id.edtMaKM);
        edtPhanTram = findViewById(R.id.edtPhanTramGiam);
        spinnerMonAn = findViewById(R.id.spinnerMonAn);
        btnThem = findViewById(R.id.btnThemKM);

        promotionDAO = new PromotionDAO(this);
        promotionProductDAO = new PromotionProductDAO(this);
        productDAO = new ProductDAO(this);

        products = productDAO.getAll();
        ArrayList<String> productNames = new ArrayList<>();
        for (Product p : products) {
            productNames.add(p.getName());
        }

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonAn.setAdapter(spinnerAdapter);

        btnThem.setOnClickListener(v -> {
            String code = edtMaKM.getText().toString().trim();
            String phanTramStr = edtPhanTram.getText().toString().trim();

            if (code.isEmpty() || phanTramStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int phanTram = Integer.parseInt(phanTramStr);
            Promotion km = new Promotion();
            km.setCode(code);
            km.setDiscountPercent(phanTram);

            long id = promotionDAO.insert(km);
            if (id > 0) {
                int productId = products.get(spinnerMonAn.getSelectedItemPosition()).getId();
                promotionProductDAO.insert((int) id, productId);
                Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
