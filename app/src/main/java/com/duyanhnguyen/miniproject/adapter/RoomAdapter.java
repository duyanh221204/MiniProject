package com.duyanhnguyen.miniproject.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.duyanhnguyen.miniproject.R;
import com.duyanhnguyen.miniproject.model.Room;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private Context context;
    private List<Room> roomList;
    private OnRoomClickListener listener;

    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    public RoomAdapter(Context context, List<Room> roomList, OnRoomClickListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        
        holder.tvRoomName.setText(room.getName());
        if (room.isRented()) {
            holder.tvTenantName.setText("Người thuê: " + room.getTenantName());
        } else {
            // Fake a match rate like the original image
            int fakeMatchRate = 85 + (position * 3 % 15); 
            holder.tvTenantName.setText(fakeMatchRate + "% Match Rate"); 
        }

        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedPrice = formatter.format(room.getPrice()) + " VND";
        String statusText = room.isRented() ? "Đã thuê" : "Còn trống";
        holder.tvPriceStatus.setText(formattedPrice + " - " + statusText);

        // Update color
        int colorVacant = ContextCompat.getColor(context, R.color.status_vacant);
        int colorRented = ContextCompat.getColor(context, R.color.status_rented);
        int targetColor = room.isRented() ? colorRented : colorVacant;
        
        // Background for the colorBar
        GradientDrawable bgShape = (GradientDrawable) holder.colorBar.getBackground();
        bgShape.setColor(targetColor);
        holder.tvPriceStatus.setTextColor(targetColor);

        // Load image from assets
        loadImageFromAssets(holder.imgRoom, "picture/" + room.getFolderName());

        holder.itemView.setOnClickListener(v -> listener.onRoomClick(room));
        holder.btnDetails.setOnClickListener(v -> listener.onRoomClick(room));
    }

    private void loadImageFromAssets(ImageView imageView, String folderPath) {
        try {
            String[] files = context.getAssets().list(folderPath);
            if (files != null && files.length > 0) {
                // Get the first image file
                String imagePath = folderPath + "/" + files[0];
                InputStream ims = context.getAssets().open(imagePath);
                Drawable d = Drawable.createFromStream(ims, null);
                imageView.setImageDrawable(d);
                ims.close();
            } else {
                imageView.setImageResource(android.R.color.darker_gray);
            }
        } catch (IOException ex) {
            imageView.setImageResource(android.R.color.darker_gray);
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public void updateData(List<Room> newRooms) {
        this.roomList = newRooms;
        notifyDataSetChanged();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView tvRoomName, tvTenantName, tvPriceStatus;
        View colorBar;
        ImageButton btnChat, btnCall, btnDetails;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvTenantName = itemView.findViewById(R.id.tvTenantName);
            tvPriceStatus = itemView.findViewById(R.id.tvPriceStatus);
            colorBar = itemView.findViewById(R.id.colorBar);
            btnChat = itemView.findViewById(R.id.btnChat);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }
    }
}
