package com.duyanhnguyen.miniproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duyanhnguyen.miniproject.adapter.RoomAdapter;
import com.duyanhnguyen.miniproject.data.RoomRepository;
import com.duyanhnguyen.miniproject.model.Room;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RoomAdapter.OnRoomClickListener {

    private RecyclerView recyclerView;
    private LinearLayout llPagination;
    private RoomAdapter adapter;
    private List<Room> allRooms;
    private List<Room> displayedRooms;

    private ChipGroup chipGroupFilter;
    private EditText etMinPrice, etMaxPrice;
    private FloatingActionButton fabAddRoom;

    // 0 = Tất cả, 1 = Còn trống, 2 = Đã thuê
    private int filterMode = 0;
    private double minPriceFilter = -1;
    private double maxPriceFilter = -1;

    private int itemsPerPage = 4;
    private int currentPage = 0;
    private int totalPages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewRooms);
        llPagination = findViewById(R.id.llPagination);
        chipGroupFilter = findViewById(R.id.chipGroupFilter);
        etMinPrice = findViewById(R.id.etMinPrice);
        etMaxPrice = findViewById(R.id.etMaxPrice);
        fabAddRoom = findViewById(R.id.fabAddRoom);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allRooms = RoomRepository.getInstance().getRooms();
        displayedRooms = new ArrayList<>(allRooms);

        adapter = new RoomAdapter(this, new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        setupFilterChips();
        setupPriceSearch();
        fabAddRoom.setOnClickListener(v -> showAddRoomDialog());

        applyFiltersAndSearch();
    }

    private void setupFilterChips() {
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipAll) {
                filterMode = 0;
            } else if (id == R.id.chipVacant) {
                filterMode = 1;
            } else if (id == R.id.chipRented) {
                filterMode = 2;
            }
            applyFiltersAndSearch();
        });
    }

    private void setupPriceSearch() {
        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            hideKeyboard(v);
            String minStr = etMinPrice.getText().toString().trim();
            String maxStr = etMaxPrice.getText().toString().trim();

            if (minStr.isEmpty() && maxStr.isEmpty()) {
                minPriceFilter = -1;
                maxPriceFilter = -1;
                applyFiltersAndSearch();
                return;
            }

            try {
                minPriceFilter = minStr.isEmpty() ? -1 : Double.parseDouble(minStr);
                maxPriceFilter = maxStr.isEmpty() ? -1 : Double.parseDouble(maxStr);

                if (minPriceFilter > 0 && maxPriceFilter > 0 && minPriceFilter > maxPriceFilter) {
                    Toast.makeText(this, "Giá từ phải nhỏ hơn hoặc bằng giá đến", Toast.LENGTH_SHORT).show();
                    return;
                }
                applyFiltersAndSearch();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFiltersAndSearch() {
        List<Room> result = new ArrayList<>(allRooms);

        // Status filter
        if (filterMode == 1) {
            List<Room> temp = new ArrayList<>();
            for (Room r : result) {
                if (!r.isRented()) temp.add(r);
            }
            result = temp;
        } else if (filterMode == 2) {
            List<Room> temp = new ArrayList<>();
            for (Room r : result) {
                if (r.isRented()) temp.add(r);
            }
            result = temp;
        }

        // Price range filter
        if (minPriceFilter >= 0) {
            List<Room> temp = new ArrayList<>();
            for (Room r : result) {
                if (r.getPrice() >= minPriceFilter) temp.add(r);
            }
            result = temp;
        }
        if (maxPriceFilter >= 0) {
            List<Room> temp = new ArrayList<>();
            for (Room r : result) {
                if (r.getPrice() <= maxPriceFilter) temp.add(r);
            }
            result = temp;
        }

        displayedRooms = result;
        totalPages = (int) Math.ceil((double) displayedRooms.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;
        loadPage(0);
    }

    private void loadPage(int pageIndex) {
        currentPage = pageIndex;
        int start = pageIndex * itemsPerPage;
        int end = Math.min(start + itemsPerPage, displayedRooms.size());
        List<Room> pageData = start < displayedRooms.size()
                ? new ArrayList<>(displayedRooms.subList(start, end))
                : new ArrayList<>();
        adapter.updateData(pageData);
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
                tvPage.setBackgroundResource(R.drawable.bg_circle_white);
                tvPage.setTextColor(Color.BLACK);
                tvPage.setTypeface(null, Typeface.BOLD);
                tvPage.setElevation(4f);
            } else {
                tvPage.setBackgroundResource(R.drawable.bg_circle_transparent);
                tvPage.setTextColor(Color.parseColor("#999999"));
                tvPage.setTypeface(null, Typeface.NORMAL);
                tvPage.setElevation(0f);
            }
            final int pageToLoad = i;
            tvPage.setOnClickListener(v -> loadPage(pageToLoad));
            llPagination.addView(tvPage);
        }
    }

    private void showAddRoomDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_room, null);

        TextInputLayout tilRoomName = dialogView.findViewById(R.id.tilRoomName);
        TextInputLayout tilPrice = dialogView.findViewById(R.id.tilPrice);
        TextInputLayout tilTenantName = dialogView.findViewById(R.id.tilTenantName);
        TextInputLayout tilPhone = dialogView.findViewById(R.id.tilPhone);

        EditText etRoomName = dialogView.findViewById(R.id.etRoomName);
        EditText etPrice = dialogView.findViewById(R.id.etPrice);
        EditText etTenantName = dialogView.findViewById(R.id.etTenantName);
        EditText etPhone = dialogView.findViewById(R.id.etPhone);

        RadioGroup rgStatus = dialogView.findViewById(R.id.rgStatus);

        rgStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbRented) {
                tilTenantName.setVisibility(View.VISIBLE);
                tilPhone.setVisibility(View.VISIBLE);
            } else {
                tilTenantName.setVisibility(View.GONE);
                tilPhone.setVisibility(View.GONE);
                tilTenantName.setError(null);
                tilPhone.setError(null);
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Thêm phòng mới")
                .setView(dialogView)
                .setPositiveButton("Thêm", null)
                .setNegativeButton("Hủy", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    if (validateAndSaveRoom(tilRoomName, tilPrice, tilTenantName, tilPhone,
                            etRoomName, etPrice, etTenantName, etPhone, rgStatus, dialog)) {
                        dialog.dismiss();
                    }
                }));

        dialog.show();
    }

    private boolean validateAndSaveRoom(
            TextInputLayout tilRoomName, TextInputLayout tilPrice,
            TextInputLayout tilTenantName, TextInputLayout tilPhone,
            EditText etRoomName, EditText etPrice,
            EditText etTenantName, EditText etPhone,
            RadioGroup rgStatus, AlertDialog dialog) {

        boolean valid = true;

        String roomName = etRoomName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        // Validate tên phòng
        if (TextUtils.isEmpty(roomName)) {
            tilRoomName.setError("Vui lòng nhập tên phòng");
            valid = false;
        } else {
            tilRoomName.setError(null);
        }

        // Validate giá thuê
        double price = 0;
        if (TextUtils.isEmpty(priceStr)) {
            tilPrice.setError("Vui lòng nhập giá thuê");
            valid = false;
        } else {
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    tilPrice.setError("Giá thuê phải lớn hơn 0");
                    valid = false;
                } else {
                    tilPrice.setError(null);
                }
            } catch (NumberFormatException e) {
                tilPrice.setError("Giá thuê không hợp lệ");
                valid = false;
            }
        }

        boolean isRented = rgStatus.getCheckedRadioButtonId() == R.id.rbRented;
        String tenantName = "";
        String phone = "";

        if (isRented) {
            tenantName = etTenantName.getText().toString().trim();
            phone = etPhone.getText().toString().trim();

            if (TextUtils.isEmpty(tenantName)) {
                tilTenantName.setError("Vui lòng nhập tên người thuê");
                valid = false;
            } else {
                tilTenantName.setError(null);
            }

            if (TextUtils.isEmpty(phone)) {
                tilPhone.setError("Vui lòng nhập số điện thoại");
                valid = false;
            } else if (!phone.matches("0[0-9]{9}")) {
                tilPhone.setError("Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)");
                valid = false;
            } else {
                tilPhone.setError(null);
            }
        }

        if (!valid) return false;

        // Cycle qua các folder ảnh có sẵn (room1 – room10)
        int nextIndex = (allRooms.size() % 10) + 1;
        String folderName = "room" + nextIndex;

        String id = RoomRepository.getInstance().generateId();
        Room newRoom = new Room(id, roomName, price, isRented, tenantName, phone, folderName);
        RoomRepository.getInstance().addRoom(newRoom);

        // Cập nhật danh sách tham chiếu
        allRooms = RoomRepository.getInstance().getRooms();

        Toast.makeText(this, "Đã thêm phòng: " + roomName, Toast.LENGTH_SHORT).show();
        applyFiltersAndSearch();
        return true;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onRoomClick(Room room) {
        Intent intent = new Intent(this, RoomDetailActivity.class);
        intent.putExtra("ROOM_ID", room.getId());
        startActivity(intent);
    }
}
