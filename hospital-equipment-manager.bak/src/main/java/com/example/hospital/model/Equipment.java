package com.example.hospital.model;

import java.time.LocalDate;

public class Equipment {
    private int id;
    private String name;
    private String model;
    private String location;
    private int quantity;
    private LocalDate lastMaintenance;
    private int maintenanceIntervalDays;

    public Equipment() {
    }

    // getters/setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(LocalDate lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

    public int getMaintenanceIntervalDays() {
        return maintenanceIntervalDays;
    }

    public void setMaintenanceIntervalDays(int maintenanceIntervalDays) {
        this.maintenanceIntervalDays = maintenanceIntervalDays;
    }
}