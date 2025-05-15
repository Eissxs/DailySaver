package com.Eissxs.dailysaver.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.Eissxs.dailysaver.R;
import com.Eissxs.dailysaver.database.AppDatabase;
import com.Eissxs.dailysaver.database.SavingDao;
import com.Eissxs.dailysaver.database.SavingEntry;
import com.Eissxs.dailysaver.database.SavingGoal;
import com.google.android.material.card.MaterialCardView;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private TextView totalAmountText;
    private TextView milestoneMessage;
    private SavingDao savingDao;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        totalAmountText = findViewById(R.id.totalAmountText);
        milestoneMessage = findViewById(R.id.milestoneMessage);
        
        // Initialize database
        savingDao = AppDatabase.getInstance(this).savingDao();
        
        // Initialize preferences
        prefs = getSharedPreferences("DailySaverPrefs", MODE_PRIVATE);

        // Set up navigation
        setupNavigation();

        // Load data
        loadSavingsData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSavingsData();
    }

    private void setupNavigation() {
        MaterialCardView addEntryCard = findViewById(R.id.addEntryCard);
        MaterialCardView historyCard = findViewById(R.id.historyCard);
        MaterialCardView goalTrackerCard = findViewById(R.id.goalTrackerCard);
        MaterialCardView settingsCard = findViewById(R.id.settingsCard);

        addEntryCard.setOnClickListener(v -> 
            startActivity(new Intent(this, AddEntryActivity.class)));

        historyCard.setOnClickListener(v -> 
            startActivity(new Intent(this, HistoryActivity.class)));

        goalTrackerCard.setOnClickListener(v -> 
            startActivity(new Intent(this, GoalTrackerActivity.class)));

        settingsCard.setOnClickListener(v -> 
            startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void loadSavingsData() {
        new Thread(() -> {
            // Get total savings
            final List<SavingEntry> entries = savingDao.getAllEntries();
            final float totalSavings = calculateTotalSavings(entries);

            // Get current goal
            final SavingGoal goal = savingDao.getGoal();
            
            // Format amount with currency
            String currencyCode = prefs.getString("currency", Currency.getInstance(Locale.getDefault()).getCurrencyCode());
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setCurrency(Currency.getInstance(currencyCode));
            final String formattedAmount = format.format(totalSavings);

            // Update UI
            runOnUiThread(() -> {
                totalAmountText.setText(formattedAmount);
                updateMilestoneMessage(totalSavings, goal);
            });
        }).start();
    }

    private float calculateTotalSavings(List<SavingEntry> entries) {
        float total = 0;
        for (SavingEntry entry : entries) {
            total += entry.amount;
        }
        return total;
    }

    private void updateMilestoneMessage(float totalSavings, SavingGoal goal) {
        if (goal == null) {
            milestoneMessage.setText(R.string.set_goal_message);
            return;
        }

        float progress = totalSavings / goal.getGoalAmount();
        if (progress >= 1.0f) {
            milestoneMessage.setText(R.string.goal_complete);
        } else if (progress >= 0.75f) {
            milestoneMessage.setText(R.string.milestone_75);
        } else if (progress >= 0.5f) {
            milestoneMessage.setText(R.string.milestone_50);
        } else if (progress >= 0.25f) {
            milestoneMessage.setText(R.string.milestone_25);
        } else {
            milestoneMessage.setText(R.string.keep_going);
        }
    }
} 