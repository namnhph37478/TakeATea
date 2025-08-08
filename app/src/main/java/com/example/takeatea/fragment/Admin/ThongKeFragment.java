package com.example.takeatea.fragment.Admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.takeatea.R;
import com.example.takeatea.dao.OrderDAO;
import com.example.takeatea.dao.OrderDetailDAO;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ThongKeFragment extends Fragment {

    private TextView tvTongDonHang, tvDoanhThu, tvTop1, tvTop2, tvTop3;
    private LineChart chartRevenue;     // nếu layout đã đổi sang LineChart
    private ImageView imgBieuDo;        // nếu vẫn là ImageView thì có biến này (không dùng)

    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;

    public ThongKeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_thong_ke, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        tvTongDonHang = v.findViewById(R.id.tvTongDonHang);
        tvDoanhThu    = v.findViewById(R.id.tvDoanhThu);
        tvTop1        = v.findViewById(R.id.tvTop1);
        tvTop2        = v.findViewById(R.id.tvTop2);
        tvTop3        = v.findViewById(R.id.tvTop3);

        // Tùy layout hiện tại có gì:
        chartRevenue  = v.findViewById(R.id.chartRevenue);

        orderDAO = new OrderDAO(requireContext());
        orderDetailDAO = new OrderDetailDAO(requireContext());

        loadTodayStats();
        loadTopProducts();

        if (chartRevenue != null) {
            renderRevenueChartLast7Days();
        }
    }

    private void loadTodayStats() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(System.currentTimeMillis());

        int totalOrders = orderDAO.countOrdersByDateText(today);     // dùng orderDate TEXT
        double totalRevenue = orderDAO.sumRevenueByDateText(today);  // dùng totalAmount REAL

        tvTongDonHang.setText(" Tổng đơn hàng hôm nay: " + totalOrders);
        tvDoanhThu.setText(" Doanh thu hôm nay: " +
                NumberFormat.getInstance(Locale.getDefault()).format(totalRevenue) + "đ");
    }

    private void loadTopProducts() {
        List<OrderDetailDAO.TopProduct> tops = orderDetailDAO.getTopProducts(3);
        if (tops.size() > 0) tvTop1.setText("1. " + tops.get(0).name + " (" + tops.get(0).totalSold + " ly)");
        if (tops.size() > 1) tvTop2.setText("2. " + tops.get(1).name + " (" + tops.get(1).totalSold + " ly)");
        if (tops.size() > 2) tvTop3.setText("3. " + tops.get(2).name + " (" + tops.get(2).totalSold + " ly)");
    }

    // ======= Chart 7 ngày gần nhất (nếu bạn đã dùng MPAndroidChart) =======
    private void renderRevenueChartLast7Days() {
        List<String> days = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 6; i >= 0; i--) {
            Calendar c = (Calendar) cal.clone();
            c.add(Calendar.DAY_OF_YEAR, -i);
            days.add(sdf.format(c.getTime()));
        }

        String start = days.get(0);
        String end   = days.get(days.size() - 1);
        Map<String, Double> map = orderDAO.sumRevenueGroupByDate(start, end); // yyyy-MM-dd -> sum

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < days.size(); i++) {
            double v = map.getOrDefault(days.get(i), 0.0);
            entries.add(new Entry(i, (float) v));
        }

        LineDataSet ds = new LineDataSet(entries, "Doanh thu 7 ngày gần nhất");
        ds.setLineWidth(2f);
        ds.setCircleRadius(3.5f);
        ds.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        ds.setDrawValues(false);

        chartRevenue.setData(new LineData(ds));

        XAxis x = chartRevenue.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1f);
        x.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat in  = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            private final SimpleDateFormat out = new SimpleDateFormat("dd/MM", Locale.getDefault());
            @Override public String getFormattedValue(float value) {
                int idx = (int) value;
                if (idx >= 0 && idx < days.size()) {
                    try { return out.format(in.parse(days.get(idx))); }
                    catch (Exception e) { return ""; }
                }
                return "";
            }
        });

        chartRevenue.getAxisRight().setEnabled(false);
        chartRevenue.getDescription().setEnabled(false);
        chartRevenue.getLegend().setEnabled(true);
        chartRevenue.animateX(600);
        chartRevenue.invalidate();
    }
}
