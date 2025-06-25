package com.app.myapplication;

public class Driver {
    private String name;
    private String phone;
    private String vehicle;
    private String location;
    private int availableSeats;  // Make sure this is an int, NOT String

    public Driver() {
        // Default constructor required for Firebase
    }

    public Driver(String name, String phone, String vehicle, int availableSeats, String location) {
        this.name = name;
        this.phone = phone;
        this.vehicle = vehicle;
        this.availableSeats = availableSeats;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getLocation() {
        return location;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }
}
