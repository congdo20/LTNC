// package com.example.hospital.models;

// import java.time.LocalDate;

// public class Equipment {
//     private int id;
//     private String name;
//     private String model;
//     private String location;
//     private int quantity;
//     private LocalDate lastMaintenance;
//     private int maintenanceIntervalDays;

//     public Equipment() {
//     }

//     // getters/setters
//     public int getId() {
//         return id;
//     }

//     public void setId(int id) {
//         this.id = id;
//     }

//     public String getName() {
//         return name;
//     }

//     public void setName(String name) {
//         this.name = name;
//     }

//     public String getModel() {
//         return model;
//     }

//     public void setModel(String model) {
//         this.model = model;
//     }

//     public String getLocation() {
//         return location;
//     }

//     public void setLocation(String location) {
//         this.location = location;
//     }

//     public int getQuantity() {
//         return quantity;
//     }

//     public void setQuantity(int quantity) {
//         this.quantity = quantity;
//     }

//     public LocalDate getLastMaintenance() {
//         return lastMaintenance;
//     }

//     public void setLastMaintenance(LocalDate lastMaintenance) {
//         this.lastMaintenance = lastMaintenance;
//     }

//     public int getMaintenanceIntervalDays() {
//         return maintenanceIntervalDays;
//     }

//     public void setMaintenanceIntervalDays(int maintenanceIntervalDays) {
//         this.maintenanceIntervalDays = maintenanceIntervalDays;
//     }
// }



// package com.example.hospital.models;

// import java.time.LocalDate;

// public class Equipment {
//     private int id;
//     private String code;
//     private String name;
//     private String manufacturer;
//     private Integer yearOfUse;
//     private String status;
//     private Integer departmentId;

//     // bổ sung
//     private String model;
//     private String location;
//     private Integer quantity;
//     private LocalDate lastMaintenance;
//     private Integer maintenanceIntervalDays;

//     public Equipment() {
//     }

//     public int getId() {
//         return id;
//     }

//     public void setId(int id) {
//         this.id = id;
//     }

//     public String getCode() {
//         return code;
//     }

//     public void setCode(String code) {
//         this.code = code;
//     }

//     public String getName() {
//         return name;
//     }

//     public void setName(String name) {
//         this.name = name;
//     }

//     public String getManufacturer() {
//         return manufacturer;
//     }

//     public void setManufacturer(String manufacturer) {
//         this.manufacturer = manufacturer;
//     }

//     public Integer getYearOfUse() {
//         return yearOfUse;
//     }

//     public void setYearOfUse(Integer yearOfUse) {
//         this.yearOfUse = yearOfUse;
//     }

//     public String getStatus() {
//         return status;
//     }

//     public void setStatus(String status) {
//         this.status = status;
//     }

//     public Integer getDepartmentId() {
//         return departmentId;
//     }

//     public void setDepartmentId(Integer departmentId) {
//         this.departmentId = departmentId;
//     }

//     public String getModel() {
//         return model;
//     }

//     public void setModel(String model) {
//         this.model = model;
//     }

//     public String getLocation() {
//         return location;
//     }

//     public void setLocation(String location) {
//         this.location = location;
//     }

//     public Integer getQuantity() {
//         return quantity;
//     }

//     public void setQuantity(Integer quantity) {
//         this.quantity = quantity;
//     }

//     public LocalDate getLastMaintenance() {
//         return lastMaintenance;
//     }

//     public void setLastMaintenance(LocalDate lastMaintenance) {
//         this.lastMaintenance = lastMaintenance;
//     }

//     public Integer getMaintenanceIntervalDays() {
//         return maintenanceIntervalDays;
//     }

//     public void setMaintenanceIntervalDays(Integer maintenanceIntervalDays) {
//         this.maintenanceIntervalDays = maintenanceIntervalDays;
//     }
// }


package com.example.hospital.models;

import java.time.LocalDate;

public class Equipment {

    private Integer id;
    private String code;
    private String name;
    private String manufacturer;
    private Integer yearOfUse;
    private String status;
    private Integer departmentId;
    private LocalDate lastMaintenance;


    public Equipment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getYearOfUse() {
        return yearOfUse;
    }

    public void setYearOfUse(Integer yearOfUse) {
        this.yearOfUse = yearOfUse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public LocalDate getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(LocalDate lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

}
