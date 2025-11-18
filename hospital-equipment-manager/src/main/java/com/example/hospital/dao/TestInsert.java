package com.example.hospital.dao;

import com.example.hospital.models.MaintenanceRequest;

public class TestInsert {
    public static void main(String[] args) throws Exception {
        MaintenanceRequestDAO dao = new MaintenanceRequestDAO();
        MaintenanceRequest r = new MaintenanceRequest();
        r.setRequesterId(2); // existing user from sample data
        r.setEquipmentId(1); // assume equipment id 1 exists
        r.setDepartmentId(1);
        r.setIssueDescription("Test insert from TestInsert");
        // r.setPriority("TRUNG_BINH");
        r.setStatus("CHO_XU_LY");

        dao.create(r);
        System.out.println("Insert attempted");
    }
}
