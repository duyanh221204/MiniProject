package com.duyanhnguyen.miniproject;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.duyanhnguyen.miniproject.adapter.ImagePagerAdapter;
import com.duyanhnguyen.miniproject.data.RoomRepository;
import com.duyanhnguyen.miniproject.model.Room;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RoomDetailActivity extends AppCompatActivity {

    private ViewPager2 imgRoomDetail;
    private TextView tvDetailRoomName, tvDetailStatus, tvTagPrice, tvDetailDescription;
    private TextView tvTenantNameCard, tvTenantPhoneCard, tvTenantLabel;
    private ImageButton btnBackDetail, btnPrev, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        initViews();
        
        String roomId = getIntent().getStringExtra("ROOM_ID");
        if (roomId != null) {
            Room room = RoomRepository.getInstance().getRoomById(roomId);
            if (room != null) {
                bindData(room);
            }
        }

        btnBackDetail.setOnClickListener(v -> finish());
        
        btnPrev.setOnClickListener(v -> {
            int current = imgRoomDetail.getCurrentItem();
            if (current > 0) imgRoomDetail.setCurrentItem(current - 1);
        });

        btnNext.setOnClickListener(v -> {
            int current = imgRoomDetail.getCurrentItem();
            if (imgRoomDetail.getAdapter() != null && current < imgRoomDetail.getAdapter().getItemCount() - 1) {
                imgRoomDetail.setCurrentItem(current + 1);
            }
        });
    }

    private void initViews() {
        imgRoomDetail = findViewById(R.id.imgRoomDetail);
        tvDetailRoomName = findViewById(R.id.tvDetailRoomName);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvTagPrice = findViewById(R.id.tvTagPrice);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvTenantNameCard = findViewById(R.id.tvTenantNameCard);
        tvTenantPhoneCard = findViewById(R.id.tvTenantPhoneCard);
        tvTenantLabel = findViewById(R.id.tvTenantLabel);
        btnBackDetail = findViewById(R.id.btnBackDetail);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
    }

    private void bindData(Room room) {
        tvDetailRoomName.setText(room.getName());
        
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedPrice = formatter.format(room.getPrice()) + " VND/tháng";
        tvTagPrice.setText(formattedPrice);

        if (room.isRented()) {
            tvDetailStatus.setText("Đã thuê");
            tvDetailStatus.setTextColor(Color.parseColor("#F56565")); // Red
            tvDetailDescription.setText("Phòng này hiện tại đã có người thuê. Các tiện ích điện và nước được tính theo giá nhà nước.");
            tvTenantNameCard.setText("Họ tên: " + room.getTenantName());
            tvTenantPhoneCard.setText("Số điện thoại: " + room.getPhoneNumber());
        } else {
            tvDetailStatus.setText("Còn trống");
            tvDetailStatus.setTextColor(Color.parseColor("#48BB78")); // Green
            tvDetailDescription.setText("Phòng này hiện tại còn trống và đang chờ người thuê mới. Giờ giấc tự do, an ninh đảm bảo.");
            tvTenantNameCard.setText("Họ tên: Không có");
            tvTenantPhoneCard.setText("Số điện thoại: Không có");
        }

        loadImagesFromAssets(room.getFolderName());
    }

    private void loadImagesFromAssets(String folderPath) {
        try {
            String[] files = getAssets().list("picture/" + folderPath);
            if (files != null && files.length > 0) {
                List<String> imagePaths = new ArrayList<>();
                for (String file : files) {
                    imagePaths.add("picture/" + folderPath + "/" + file);
                }
                ImagePagerAdapter adapter = new ImagePagerAdapter(this, imagePaths);
                imgRoomDetail.setAdapter(adapter);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
