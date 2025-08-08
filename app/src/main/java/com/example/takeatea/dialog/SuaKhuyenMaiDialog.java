package com.example.takeatea.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.takeatea.R;
import com.example.takeatea.dao.ProductDAO;
import com.example.takeatea.dao.PromotionDAO;
import com.example.takeatea.dao.PromotionProductDAO;
import com.example.takeatea.model.Product;
import com.example.takeatea.model.Promotion;

import java.util.ArrayList;

public class SuaKhuyenMaiDialog extends Dialog {

    private final Promotion km;
    private EditText edtMaKM, edtPhanTram;
    private Spinner spinnerMon;
    private PromotionDAO promotionDAO;
    private PromotionProductDAO promotionProductDAO;
    private ProductDAO productDAO;
    private ArrayList<Product> products;

    public SuaKhuyenMaiDialog(Context context, Promotion km) {
        super(context);
        this.km = km;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_admin_sua_khuyen_mai);

        edtMaKM = findViewById(R.id.edtMaKM);
        edtPhanTram = findViewById(R.id.edtPhanTramGiam);
        spinnerMon = findViewById(R.id.spinnerMonApDung);
        Button btnLuu = findViewById(R.id.btnLuu);

        promotionDAO = new PromotionDAO(getContext());
        promotionProductDAO = new PromotionProductDAO(getContext());
        productDAO = new ProductDAO(getContext());

        products = productDAO.getAll();
        ArrayList<String> names = new ArrayList<>();
        for (Product p : products) {
            names.add(p.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMon.setAdapter(adapter);

        edtMaKM.setText(km.getCode());
        edtPhanTram.setText(String.valueOf(km.getDiscountPercent()));

        btnLuu.setOnClickListener(v -> {
            String code = edtMaKM.getText().toString().trim();
            int phanTram = Integer.parseInt(edtPhanTram.getText().toString().trim());
            int productId = products.get(spinnerMon.getSelectedItemPosition()).getId();

            km.setCode(code);
            km.setDiscountPercent(phanTram);
            promotionDAO.update(km);

            promotionProductDAO.deleteByPromotionId(km.getId());
            promotionProductDAO.insert(km.getId(), productId);

            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }
}
