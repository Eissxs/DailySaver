package com.Eissxs.dailysaver.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import com.Eissxs.dailysaver.R;
import com.Eissxs.dailysaver.utils.NotificationReceiver;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "DailySaverPrefs";
    private static final String PREF_CURRENCY = "currency";
    private static final String PREF_NOTIFICATIONS = "notifications_enabled";
    private static final String PREF_NOTIFICATION_TIME = "notification_time";
    
    private SharedPreferences prefs;
    private AutoCompleteTextView currencyInput;
    private SwitchMaterial notificationSwitch;
    private TextInputLayout timePickerLayout;
    private TextInputEditText timeInput;
    private Calendar notificationTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize preferences
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Initialize views
        currencyInput = findViewById(R.id.currencyInput);
        notificationSwitch = findViewById(R.id.notificationSwitch);
        timePickerLayout = findViewById(R.id.timePickerLayout);
        timeInput = findViewById(R.id.timeInput);

        // Setup currency selector
        setupCurrencySelector();
        
        // Setup notification controls
        setupNotificationControls();
    }

    private void setupCurrencySelector() {
        // Get available currencies
        Set<String> currencies = Currency.getAvailableCurrencies().stream()
            .map(Currency::getCurrencyCode)
            .collect(Collectors.toCollection(TreeSet::new));

        // Create and set adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            R.layout.item_dropdown,
            new ArrayList<>(currencies)
        );
        currencyInput.setAdapter(adapter);

        // Set current value
        String currentCurrency = prefs.getString(PREF_CURRENCY, Currency.getInstance(Locale.getDefault()).getCurrencyCode());
        currencyInput.setText(currentCurrency, false);

        // Save on selection
        currencyInput.setOnItemClickListener((parent, view, position, id) -> {
            String selected = adapter.getItem(position);
            prefs.edit().putString(PREF_CURRENCY, selected).apply();
        });
    }

    private void setupNotificationControls() {
        // Initialize notification time
        long savedTime = prefs.getLong(PREF_NOTIFICATION_TIME, getDefaultNotificationTime());
        notificationTime = Calendar.getInstance();
        notificationTime.setTimeInMillis(savedTime);

        // Update time display
        updateTimeDisplay();

        // Set current notification state
        boolean notificationsEnabled = prefs.getBoolean(PREF_NOTIFICATIONS, false);
        notificationSwitch.setChecked(notificationsEnabled);
        timePickerLayout.setVisibility(notificationsEnabled ? View.VISIBLE : View.GONE);

        // Handle notification toggle
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_NOTIFICATIONS, isChecked).apply();
            timePickerLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                scheduleNotification();
            } else {
                cancelNotification();
            }
        });

        // Handle time selection
        timeInput.setOnClickListener(v -> showTimePickerDialog());
    }

    private void showTimePickerDialog() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            notificationTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            notificationTime.set(Calendar.MINUTE, minute);
            
            // Save time
            prefs.edit().putLong(PREF_NOTIFICATION_TIME, notificationTime.getTimeInMillis()).apply();
            
            // Update display
            updateTimeDisplay();
            
            // Reschedule if enabled
            if (notificationSwitch.isChecked()) {
                scheduleNotification();
            }
        }, notificationTime.get(Calendar.HOUR_OF_DAY), notificationTime.get(Calendar.MINUTE), false).show();
    }

    private void updateTimeDisplay() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        timeInput.setText(timeFormat.format(notificationTime.getTime()));
    }

    private long getDefaultNotificationTime() {
        Calendar defaultTime = Calendar.getInstance();
        defaultTime.set(Calendar.HOUR_OF_DAY, 20); // 8 PM
        defaultTime.set(Calendar.MINUTE, 0);
        return defaultTime.getTimeInMillis();
    }

    private void scheduleNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Set alarm to repeat daily at specified time
        Calendar calendar = (Calendar) notificationTime.clone();
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        );
    }

    private void cancelNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }
} 