package com.example.takeatea.fragment.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.adapter.KhuyenMaiAdapter;
import com.example.takeatea.dao.PromotionDAO;
import com.example.takeatea.model.Promotion;
import com.example.takeatea.activity.ThemKhuyenMaiActivity;

import java.util.ArrayList;

public class KhuyenMaiFragment extends Fragment {

    private RecyclerView rcvKhuyenMai;
    private Button btnThemKhuyenMai;
    private PromotionDAO promotionDAO;
    private KhuyenMaiAdapter adapter;
    private ArrayList<Promotion> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_khuyen_mai, container, false);

        rcvKhuyenMai = view.findViewById(R.id.rcvKhuyenMai);
        btnThemKhuyenMai = view.findViewById(R.id.btnThemKhuyenMai);

        promotionDAO = new PromotionDAO(getContext());
        list = promotionDAO.getAll();

        adapter = new KhuyenMaiAdapter(getContext(), list);
        rcvKhuyenMai.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvKhuyenMai.setAdapter(adapter);

        btnThemKhuyenMai.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ThemKhuyenMaiActivity.class));
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        list.addAll(promotionDAO.getAll());
        adapter.notifyDataSetChanged();
    }
}
