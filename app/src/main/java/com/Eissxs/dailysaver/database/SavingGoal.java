package com.Eissxs.dailysaver.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "saving_goals")
public class SavingGoal {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "goal_amount")
    public float goalAmount;

    // Constructor
    public SavingGoal(float goalAmount) {
        this.goalAmount = goalAmount;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public float getGoalAmount() { return goalAmount; }
    public void setGoalAmount(float goalAmount) { this.goalAmount = goalAmount; }
} 