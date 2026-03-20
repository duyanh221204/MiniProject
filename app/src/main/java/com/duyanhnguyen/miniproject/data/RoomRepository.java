package com.duyanhnguyen.miniproject.data;

import com.duyanhnguyen.miniproject.model.Room;
import java.util.ArrayList;
import java.util.List;

public class RoomRepository {
    private static RoomRepository instance;
    private List<Room> roomList;

    private RoomRepository() {
        roomList = new ArrayList<>();
        // Mock data matching the user's requirements
        roomList.add(new Room("R01", "Phòng 101", 1500000, false, "", "", "room1"));
        roomList.add(new Room("R02", "Phòng 102", 2000000, true, "Nguyễn Văn A", "0912345678", "room2"));
        roomList.add(new Room("R03", "Phòng 201", 1800000, false, "", "", "room3"));
        roomList.add(new Room("R04", "Phòng 202", 2500000, true, "Trần Thị B", "0987654321", "room4"));
        roomList.add(new Room("R05", "Phòng 301", 1500000, false, "", "", "room5"));
        roomList.add(new Room("R06", "Phòng 302", 2200000, true, "Lê Văn C", "0901234567", "room6"));
        roomList.add(new Room("R07", "Phòng 401", 1700000, false, "", "", "room7"));
        roomList.add(new Room("R08", "Phòng 402", 3000000, true, "Phạm Thị D", "0934567890", "room8"));
        roomList.add(new Room("R09", "Phòng 501", 1600000, false, "", "", "room9"));
        roomList.add(new Room("R10", "Phòng 502", 3500000, true, "Hoàng Văn E", "0976543210", "room10"));
    }

    public static RoomRepository getInstance() {
        if (instance == null) {
            instance = new RoomRepository();
        }
        return instance;
    }

    public List<Room> getRooms() {
        return roomList;
    }
    
    public Room getRoomById(String id) {
        for (Room r : roomList) {
            if (r.getId().equals(id)) return r;
        }
        return null;
    }

    public void addRoom(Room room) {
        roomList.add(room);
    }

    public String generateId() {
        return "R" + String.format("%02d", roomList.size() + 1);
    }
}
