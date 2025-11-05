package com.example.hospital.models;

import java.time.LocalDateTime;

public class Tx {
    private int id;
    private int itemId;
    private LocalDateTime txDate;
    private int changeQty;
    private String note;

    public Tx() {
    }

    // getters / setters...
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public LocalDateTime getTxDate() {
        return txDate;
    }

    public void setTxDate(LocalDateTime txDate) {
        this.txDate = txDate;
    }

    public int getChangeQty() {
        return changeQty;
    }

    public void setChangeQty(int changeQty) {
        this.changeQty = changeQty;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
