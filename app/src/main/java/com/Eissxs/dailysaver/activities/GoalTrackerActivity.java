package com.Eissxs.dailysaver.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.Eissxs.dailysaver.R;
import com.Eissxs.dailysaver.database.AppDatabase;
import com.Eissxs.dailysaver.database.SavingDao;
import com.Eissxs.dailysaver.database.SavingEntry;
import com.Eissxs.dailysaver.database.SavingGoal;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GoalTrackerActivity extends AppCompatActivity {
    private CircularProgressIndicator progressRing;
    private TextView progressText;
    private TextView goalText;
    private MaterialButton setGoalButton;
    private LineChart trendChart;
    private SavingDao savingDao;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_tracker);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize views
        progressRing = findViewById(R.id.progressRing);
        progressText = findViewById(R.id.progressText);
        goalText = findViewById(R.id.goalText);
        setGoalButton = findViewById(R.id.setGoalButton);
        trendChart = findViewById(R.id.trendChart);

        // Initialize database
        savingDao = AppDatabase.getInstance(this).savingDao();

        // Set up currency format
        SharedPreferences prefs = getSharedPreferences("DailySaverPrefs", MODE_PRIVATE);
        String currencyCode = prefs.getString("currency", 
            Currency.getInstance(Locale.getDefault()).getCurrencyCode());
        currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setCurrency(Currency.getInstance(currencyCode));

        // Set up chart
        setupChart();

        // Set up goal button
        setGoalButton.setOnClickListener(v -> showSetGoalDialog());

        // Load data
        loadData();
    }

    private void setupChart() {
        trendChart.getDescription().setEnabled(false);
        trendChart.setTouchEnabled(true);
        trendChart.setDragEnabled(true);
        trendChart.setScaleEnabled(true);
        trendChart.setPinchZoom(true);
        trendChart.setDrawGridBackground(false);
        trendChart.setBackgroundColor(Color.TRANSPARENT);
        trendChart.getLegend().setEnabled(false);

        // Customize X axis
        XAxis xAxis = trendChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(getResources().getColor(R.color.text_secondary));
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        // Customize Y axis
        trendChart.getAxisLeft().setTextColor(getResources().getColor(R.color.text_secondary));
        trendChart.getAxisLeft().setDrawGridLines(false);
        trendChart.getAxisRight().setEnabled(false);
    }

    private void loadData() {
        new Thread(() -> {
            // Get total savings and goal
            final List<SavingEntry> entries = savingDao.getAllEntries();
            final float totalSavings = calculateTotalSavings(entries);
            final SavingGoal goal = savingDao.getGoal();

            // Update UI
            runOnUiThread(() -> {
                updateProgress(totalSavings, goal);
                updateChart(entries);
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

    private void updateProgress(float totalSavings, SavingGoal goal) {
        String formattedTotal = currencyFormat.format(totalSavings);
        progressText.setText(formattedTotal);

        if (goal != null) {
            float progress = Math.min((totalSavings / goal.goalAmount) * 100, 100);
            progressRing.setProgress((int) progress);
            String formattedGoal = currencyFormat.format(goal.goalAmount);
            goalText.setText(getString(R.string.goal_amount_format, formattedGoal));
            setGoalButton.setText(R.string.update_goal);
        } else {
            progressRing.setProgress(0);
            goalText.setText(R.string.no_goal_set);
            setGoalButton.setText(R.string.set_goal);
        }
    }

    private void updateChart(List<SavingEntry> entries) {
        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
        float runningTotal = 0;

        for (int i = 0; i < entries.size(); i++) {
            SavingEntry entry = entries.get(i);
            runningTotal += entry.amount;
            values.add(new Entry(i, runningTotal));
            labels.add(dateFormat.format(entry.date));
        }

        // Create dataset
        LineDataSet dataSet = new LineDataSet(values, "Savings Trend");
        dataSet.setColor(getResources().getColor(R.color.primary_accent));
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getResources().getColor(R.color.primary_accent));
        dataSet.setFillAlpha(50);

        // Set data
        LineData lineData = new LineData(dataSet);
        trendChart.setData(lineData);

        // Set labels
        XAxis xAxis = trendChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(Math.min(labels.size(), 6));

        trendChart.invalidate();
    }

    private void showSetGoalDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_goal, null);
        EditText goalInput = dialogView.findViewById(R.id.goalInput);

        new AlertDialog.Builder(this)
            .setTitle(R.string.set_goal)
            .setView(dialogView)
            .setPositiveButton(R.string.save, (dialog, which) -> {
                String input = goalInput.getText().toString();
                if (!input.isEmpty()) {
                    try {
                        float goalAmount = Float.parseFloat(input);
                        if (goalAmount > 0) {
                            saveGoal(goalAmount);
                        }
                    } catch (NumberFormatException e) {
                        // Invalid input
                    }
                }
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }

    private void saveGoal(float amount) {
        new Thread(() -> {
            SavingGoal goal = savingDao.getGoal();
            if (goal == null) {
                goal = new SavingGoal(amount);
                savingDao.insertGoal(goal);
            } else {
                goal.goalAmount = amount;
                savingDao.updateGoal(goal);
            }
            loadData();
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 