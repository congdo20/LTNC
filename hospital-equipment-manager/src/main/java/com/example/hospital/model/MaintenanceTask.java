// package com.example.hospital.model;

// import java.time.LocalDate;

// public class MaintenanceTask {
//     private int id;
//     private int itemId;
//     private LocalDate scheduledDate;
//     private boolean done;
//     private LocalDate doneDate;
//     private String note;

//     public MaintenanceTask() {
//     }

//     // getters/setters
//     public int getId() {
//         return id;
//     }

//     public void setId(int id) {
//         this.id = id;
//     }

//     public int getItemId() {
//         return itemId;
//     }

//     public void setItemId(int itemId) {
//         this.itemId = itemId;
//     }

//     public LocalDate getScheduledDate() {
//         return scheduledDate;
//     }

//     public void setScheduledDate(LocalDate scheduledDate) {
//         this.scheduledDate = scheduledDate;
//     }

//     public boolean isDone() {
//         return done;
//     }

//     public void setDone(boolean done) {
//         this.done = done;
//     }

//     public LocalDate getDoneDate() {
//         return doneDate;
//     }

//     public void setDoneDate(LocalDate doneDate) {
//         this.doneDate = doneDate;
//     }

//     public String getNote() {
//         return note;
//     }

//     public void setNote(String note) {
//         this.note = note;
//     }
// }


package com.example.hospital.model;

import java.time.LocalDate;

public class MaintenanceTask {
    private int id;
    private int equipmentId;
    private LocalDate scheduleDate;
    private boolean completed;
    private String note;
    private boolean inspected;         
    private boolean accepted;        
    private String inspectionNote;     
    private String acceptedBy;         
    private LocalDate acceptanceDate;
    private String assignedTo; // who/team is assigned to perform the maintenance
    public MaintenanceTask() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    public boolean isInspected() { 
        return inspected; 
    }

    public void setInspected(boolean inspected) { 
        this.inspected = inspected; 
    }

    public boolean isAccepted() { 
        return accepted; 
    }

    public void setAccepted(boolean accepted) { 
        this.accepted = accepted; 
    }

    public String getInspectionNote() { 
        return inspectionNote; 
    }

    public void setInspectionNote(String inspectionNote) { 
        this.inspectionNote = inspectionNote; 
    }

    public String getAcceptedBy() { 
        return acceptedBy; 
    }
    public void setAcceptedBy(String acceptedBy) { 
        this.acceptedBy = acceptedBy; 
    }

    public LocalDate getAcceptanceDate() { 
        return acceptanceDate; 
    }
    public void setAcceptanceDate(LocalDate acceptanceDate) { 
        this.acceptanceDate = acceptanceDate; 
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}