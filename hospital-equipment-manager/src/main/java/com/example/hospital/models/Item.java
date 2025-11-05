package com.example.hospital.models;

import java.time.LocalDate;

public class Item {
    private int id;
    private String code;
    private String name;
    private int quantity;
    private int minStock;
    private int maintenanceIntervalDays;
    private LocalDate lastMaintenance;

    public Item() {
    }

    public Item(int id, String code, String name, int quantity, int minStock, int maintenanceIntervalDays,
            LocalDate lastMaintenance) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.minStock = minStock;
        this.maintenanceIntervalDays = maintenanceIntervalDays;
        this.lastMaintenance = lastMaintenance;
    }

    // getters / setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMinStock() {
        return minStock;
    }

    public void setMinStock(int minStock) {
        this.minStock = minStock;
    }

    public int getMaintenanceIntervalDays() {
        return maintenanceIntervalDays;
    }

    public void setMaintenanceIntervalDays(int maintenanceIntervalDays) {
        this.maintenanceIntervalDays = maintenanceIntervalDays;
    }

    public LocalDate getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(LocalDate lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

    @Override
    public String toString() {
        return code + " - " + name + " (qty=" + quantity + ")";
    }
}
