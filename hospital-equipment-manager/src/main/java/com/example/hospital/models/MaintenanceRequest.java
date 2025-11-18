// package com.example.hospital.models;

// import java.time.LocalDate;

// public class MaintenanceRequest {

//     private int id;
//     private int requesterId;
//     private int departmentId;
//     private int equipmentId;
//     private String issueDescription;
//     private String priority;
//     private String status;
//     private LocalDate requestDate;

//     public MaintenanceRequest() {
//         this.requestDate = LocalDate.now();
//         this.status = "PENDING";
//     }

//     public int getId() {
//         return id;
//     }

//     public void setId(int id) {
//         this.id = id;
//     }

//     public int getRequesterId() {
//         return requesterId;
//     }

//     public void setRequesterId(int requesterId) {
//         this.requesterId = requesterId;
//     }

//     public int getDepartmentId() {
//         return departmentId;
//     }

//     public void setDepartmentId(int departmentId) {
//         this.departmentId = departmentId;
//     }

//     public int getEquipmentId() {
//         return equipmentId;
//     }

//     public void setEquipmentId(int equipmentId) {
//         this.equipmentId = equipmentId;
//     }

//     public String getIssueDescription() {
//         return issueDescription;
//     }

//     public void setIssueDescription(String issueDescription) {
//         this.issueDescription = issueDescription;
//     }

//     public String getPriority() {
//         return priority;
//     }

//     public void setPriority(String priority) {
//         this.priority = priority;
//     }

//     public String getStatus() {
//         return status;
//     }

//     public void setStatus(String status) {
//         this.status = status;
//     }

//     public LocalDate getRequestDate() {
//         return requestDate;
//     }

//     public void setRequestDate(LocalDate requestDate) {
//         this.requestDate = requestDate;
//     }
// }

package com.example.hospital.models;

import java.time.LocalDateTime;

public class MaintenanceRequest {

    private int id;
    private int requesterId;
    private int departmentId;
    private int equipmentId;
    private String issueDescription;
    private LocalDateTime requestDate;
    private String status; // CHO_XU_LY / DA_LAP_KE_HOACH / DA_TU_CHOI

    public MaintenanceRequest() {
        this.requestDate = LocalDateTime.now();
        this.status = "CHO_XU_LY";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
