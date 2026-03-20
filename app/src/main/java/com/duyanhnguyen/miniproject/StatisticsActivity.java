package com.duyanhnguyen.miniproject;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.duyanhnguyen.miniproject.data.RoomRepository;
import com.duyanhnguyen.miniproject.model.Room;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvTotalRooms, tvRentedRooms, tvVacantRooms, tvRevenue;
    private TextView tvOccupancyRate, tvOccupancyDesc, tvOccupancyStatus, tvMonth;
    private ProgressBar progressOccupancy;
    private BarChartView barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        tvTotalRooms      = findViewById(R.id.tvTotalRooms);
        tvRentedRooms     = findViewById(R.id.tvRentedRooms);
        tvVacantRooms     = findViewById(R.id.tvVacantRooms);
        tvRevenue         = findViewById(R.id.tvRevenue);
        tvOccupancyRate   = findViewById(R.id.tvOccupancyRate);
        tvOccupancyDesc   = findViewById(R.id.tvOccupancyDesc);
        tvOccupancyStatus = findViewById(R.id.tvOccupancyStatus);
        tvMonth           = findViewById(R.id.tvMonth);
        progressOccupancy = findViewById(R.id.progressOccupancy);
        barChart          = findViewById(R.id.barChart);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setMonthLabel();
        loadStatistics();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics();
    }

    private void setMonthLabel() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year  = cal.get(Calendar.YEAR);
        tvMonth.setText("Tháng " + month + "/" + year);
    }

    private void loadStatistics() {
        List<Room> rooms = RoomRepository.getInstance().getRooms();

        int total  = rooms.size();
        int rented = 0;
        double revenue = 0.0;

        for (Room room : rooms) {
            if (room.isRented()) {
                rented++;
                revenue += room.getPrice();
            }
        }

        int vacant          = total - rented;
        double occupancyPct = total > 0 ? (rented * 100.0 / total) : 0.0;

        // Update stat cards
        tvTotalRooms.setText(String.valueOf(total));
        tvRentedRooms.setText(String.valueOf(rented));
        tvVacantRooms.setText(String.valueOf(vacant));

        // Format revenue: show in triệu if >= 1 000 000
        tvRevenue.setText(formatRevenue(revenue));

        // Occupancy
        int pct = (int) Math.round(occupancyPct);
        tvOccupancyRate.setText(pct + "%");
        tvOccupancyDesc.setText(rented + " / " + total + " phòng đã được thuê");
        tvOccupancyStatus.setText(occupancyLabel(pct));
        progressOccupancy.setProgress(pct);

        // Bar chart
        barChart.setData(rented, vacant);
    }

    private String formatRevenue(double amount) {
        if (amount >= 1_000_000) {
            double millions = amount / 1_000_000.0;
            // Show up to 1 decimal if needed
            String formatted = (millions == Math.floor(millions))
                    ? String.valueOf((long) millions)
                    : String.format(Locale.getDefault(), "%.1f", millions);
            return formatted + " triệu VND";
        }
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return nf.format((long) amount) + " VND";
    }

    private String occupancyLabel(int pct) {
        if (pct >= 90) return "Rất tốt – gần kín phòng 🎉";
        if (pct >= 70) return "Tốt – đa số phòng đã thuê";
        if (pct >= 50) return "Trung bình – còn nhiều phòng trống";
        if (pct > 0)   return "Thấp – cần tìm thêm khách thuê";
        return "Chưa có phòng nào được thuê";
    }
}
