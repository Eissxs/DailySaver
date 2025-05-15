package com.Eissxs.dailysaver.database;

import androidx.room.*;
import java.util.List;

@Dao
public interface SavingDao {
    @Insert
    void insertEntry(SavingEntry entry);

    @Update
    void updateEntry(SavingEntry entry);

    @Delete
    void deleteEntry(SavingEntry entry);

    @Query("SELECT * FROM saving_entries ORDER BY date DESC")
    List<SavingEntry> getAllEntries();

    @Insert
    void insertGoal(SavingGoal goal);

    @Query("SELECT * FROM saving_goals LIMIT 1")
    SavingGoal getGoal();

    @Update
    void updateGoal(SavingGoal goal);
} 