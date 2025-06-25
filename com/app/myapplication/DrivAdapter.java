package com.app.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DrivAdapter extends RecyclerView.Adapter<DrivAdapter.DriverViewHolder> {

    private List<Driver> driverList;

    public DrivAdapter(List<Driver> driverList) {
        this.driverList = driverList;
    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_item, parent, false);
        return new DriverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverViewHolder holder, int position) {
        Driver driver = driverList.get(position);
        holder.name.setText(driver.getName());
        holder.phone.setText("Phone: " + driver.getPhone());
        holder.vehicle.setText("Vehicle: " + driver.getVehicle());
        holder.location.setText("Location: " + driver.getLocation());
        holder.seats.setText("Available Seats: " + driver.getAvailableSeats());
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public static class DriverViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, vehicle, location, seats;

        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.driverName);
            phone = itemView.findViewById(R.id.driverPhone);
            vehicle = itemView.findViewById(R.id.driverVehicle);
            location = itemView.findViewById(R.id.driverLocation);
            seats = itemView.findViewById(R.id.driverSeats);
        }
    }
}
