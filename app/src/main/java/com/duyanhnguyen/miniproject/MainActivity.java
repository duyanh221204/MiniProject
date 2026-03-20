package com.duyanhnguyen.miniproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duyanhnguyen.miniproject.adapter.RoomAdapter;
import com.duyanhnguyen.miniproject.data.RoomRepository;
import com.duyanhnguyen.miniproject.model.Room;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RoomAdapter.OnRoomClickListener {

    private RecyclerView recyclerView;
    private LinearLayout llPagination;
    private RoomAdapter adapter;
    private List<Room> allRooms;
    
    private int itemsPerPage = 4;
    private int currentPage = 0;
    private int totalPages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewRooms);
        llPagination = findViewById(R.id.llPagination);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allRooms = RoomRepository.getInstance().getRooms();
        totalPages = (int) Math.ceil((double) allRooms.size() / itemsPerPage);

        adapter = new RoomAdapter(this, new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        loadPage(0);
    }

    private void loadPage(int pageIndex) {
        currentPage = pageIndex;
        
        int start = pageIndex * itemsPerPage;
        int end = Math.min(start + itemsPerPage, allRooms.size());

        List<Room> pageData = new ArrayList<>(allRooms.subList(start, end));
        adapter.updateData(pageData); // We need to add this method in RoomAdapter

        setupPaginationUI();
    }

    private void setupPaginationUI() {
        llPagination.removeAllViews();

        for (int i = 0; i < totalPages; i++) {
            TextView tvPage = new TextView(this);
            tvPage.setText(String.valueOf(i + 1));
            tvPage.setTextSize(16);
            tvPage.setGravity(Gravity.CENTER);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, 120);
            params.setMargins(8, 0, 8, 0);
            tvPage.setLayoutParams(params);

            if (i == currentPage) {
                // Active page styling
                tvPage.setBackgroundResource(R.drawable.bg_circle_white);
                tvPage.setTextColor(Color.BLACK);
                tvPage.setTypeface(null, Typeface.BOLD);
                tvPage.setElevation(4f);
            } else {
                // Inactive page styling
                tvPage.setBackgroundResource(R.drawable.bg_circle_transparent); // We need to create this transparent circle
                tvPage.setTextColor(Color.parseColor("#999999"));
                tvPage.setTypeface(null, Typeface.NORMAL);
                tvPage.setElevation(0f);
            }

            final int pageToLoad = i;
            tvPage.setOnClickListener(v -> loadPage(pageToLoad));
            llPagination.addView(tvPage);
        }
    }

    @Override
    public void onRoomClick(Room room) {
        Intent intent = new Intent(this, RoomDetailActivity.class);
        intent.putExtra("ROOM_ID", room.getId());
        startActivity(intent);
    }
}