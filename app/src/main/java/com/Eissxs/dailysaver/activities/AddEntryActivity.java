package com.Eissxs.dailysaver.activities;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.Eissxs.dailysaver.R;
import com.Eissxs.dailysaver.database.AppDatabase;
import com.Eissxs.dailysaver.database.SavingDao;
import com.Eissxs.dailysaver.database.SavingEntry;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Date;

public class AddEntryActivity extends AppCompatActivity {
    private TextInputEditText amountInput;
    private TextInputEditText noteInput;
    private SavingDao savingDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        
        // Set window animations
        overridePendingTransition(R.anim.slide_up, R.anim.fade_out);

        // Initialize views
        amountInput = findViewById(R.id.amountInput);
        noteInput = findViewById(R.id.noteInput);
        MaterialButton saveButton = findViewById(R.id.saveButton);
        MaterialButton cancelButton = findViewById(R.id.cancelButton);

        // Initialize database
        savingDao = AppDatabase.getInstance(this).savingDao();

        // Set click listeners
        saveButton.setOnClickListener(v -> saveEntry());
        cancelButton.setOnClickListener(v -> finish());

        // Set up modal animation
        View rootView = findViewById(android.R.id.content);
        rootView.setAlpha(0f);
        rootView.animate()
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();
    }

    private void saveEntry() {
        String amountStr = amountInput.getText().toString().trim();
        String note = noteInput.getText().toString().trim();

        if (amountStr.isEmpty()) {
            amountInput.setError(getString(R.string.enter_amount));
            return;
        }

        try {
            float amount = Float.parseFloat(amountStr);
            if (amount <= 0) {
                amountInput.setError(getString(R.string.enter_amount));
                return;
            }

            // Create and save entry
            SavingEntry entry = new SavingEntry();
            entry.amount = amount;
            entry.note = note;
            entry.date = new Date();

            new Thread(() -> {
                savingDao.insertEntry(entry);
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.entry_saved, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();

        } catch (NumberFormatException e) {
            amountInput.setError(getString(R.string.enter_amount));
        }
    }

    @Override
    public void finish() {
        View rootView = findViewById(android.R.id.content);
        rootView.animate()
            .alpha(0f)
            .setDuration(200)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> {
                super.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
            })
            .start();
    }
} 