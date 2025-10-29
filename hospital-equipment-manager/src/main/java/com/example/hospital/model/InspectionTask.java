package com.example.hospital.model;

import java.time.LocalDate;

public class InspectionTask {
    private int id;
    private int maintenanceId;
    private int equipmentId;
    private LocalDate inspectionDate;
    private String inspector;
    private boolean result;
    private String note;
    private String acceptedBy;
    private LocalDate acceptanceDate;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMaintenanceId() { return maintenanceId; }
    public void setMaintenanceId(int maintenanceId) { this.maintenanceId = maintenanceId; }

    public int getEquipmentId() { return equipmentId; }
    public void setEquipmentId(int equipmentId) { this.equipmentId = equipmentId; }

    public LocalDate getInspectionDate() { return inspectionDate; }
    public void setInspectionDate(LocalDate inspectionDate) { this.inspectionDate = inspectionDate; }

    public String getInspector() { return inspector; }
    public void setInspector(String inspector) { this.inspector = inspector; }

    public boolean isResult() { return result; }
    public void setResult(boolean result) { this.result = result; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getAcceptedBy() { return acceptedBy; }
    public void setAcceptedBy(String acceptedBy) { this.acceptedBy = acceptedBy; }

    public LocalDate getAcceptanceDate() { return acceptanceDate; }
    public void setAcceptanceDate(LocalDate acceptanceDate) { this.acceptanceDate = acceptanceDate; }
}
