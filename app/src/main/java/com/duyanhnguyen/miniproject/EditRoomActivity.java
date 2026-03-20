package com.duyanhnguyen.miniproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.duyanhnguyen.miniproject.data.RoomRepository;
import com.duyanhnguyen.miniproject.model.Room;
import com.google.android.material.textfield.TextInputEditText;

public class EditRoomActivity extends AppCompatActivity {
    private TextInputEditText etRoomName, etPrice, etTenantName, etPhoneNumber;
    private CheckBox cbIsRented;
    private Button btnSave, btnCancel;

    private Room currentRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        initViews();

        String roomId = getIntent().getStringExtra("ROOM_ID");
        if (roomId != null) {
            currentRoom = RoomRepository.getInstance().getRoomById(roomId);
            if (currentRoom != null) {
                bindData();
            }
        }

        cbIsRented.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etTenantName.setEnabled(isChecked);
            etPhoneNumber.setEnabled(isChecked);
        });

        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            if (currentRoom != null) {
                saveData();
            }
        });
    }

    private void initViews() {
        etRoomName = findViewById(R.id.etRoomName);
        etPrice = findViewById(R.id.etPrice);
        etTenantName = findViewById(R.id.etTenantName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        cbIsRented = findViewById(R.id.cbIsRented);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void bindData() {
        etRoomName.setText(currentRoom.getName());
        etPrice.setText(String.valueOf((long) currentRoom.getPrice()));
        cbIsRented.setChecked(currentRoom.isRented());
        
        etTenantName.setEnabled(currentRoom.isRented());
        etPhoneNumber.setEnabled(currentRoom.isRented());
        
        if (currentRoom.isRented()) {
            etTenantName.setText(currentRoom.getTenantName());
            etPhoneNumber.setText(currentRoom.getPhoneNumber());
        }
    }

    private void saveData() {
        String name = etRoomName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        boolean isRented = cbIsRented.isChecked();
        String tenantName = etTenantName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và giá phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = 0;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá phòng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        currentRoom.setName(name);
        currentRoom.setPrice(price);
        currentRoom.setRented(isRented);
        if (isRented) {
            currentRoom.setTenantName(tenantName);
            currentRoom.setPhoneNumber(phoneNumber);
        } else {
            currentRoom.setTenantName("");
            currentRoom.setPhoneNumber("");
        }

        RoomRepository.getInstance().updateRoom(currentRoom);
        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        finish();
    }
}
