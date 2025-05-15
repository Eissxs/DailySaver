package com.Eissxs.dailysaver.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import java.util.Date;

@Entity(tableName = "saving_entries")
public class SavingEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "amount")
    public float amount;

    @ColumnInfo(name = "note")
    public String note;

    @ColumnInfo(name = "date")
    public Date date;

    // No-args constructor required by Room
    public SavingEntry() {
        this.date = new Date();
    }

    // Constructor for manual entry creation
    @Ignore
    public SavingEntry(float amount, String note) {
        this.amount = amount;
        this.note = note;
        this.date = new Date();
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
} 