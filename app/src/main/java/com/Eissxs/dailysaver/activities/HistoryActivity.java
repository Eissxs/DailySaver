package com.Eissxs.dailysaver.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.Eissxs.dailysaver.R;
import com.Eissxs.dailysaver.adapters.SavingEntryAdapter;
import com.Eissxs.dailysaver.database.AppDatabase;
import com.Eissxs.dailysaver.database.SavingDao;
import com.Eissxs.dailysaver.database.SavingEntry;
import com.google.android.material.snackbar.Snackbar;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private View emptyState;
    private SavingEntryAdapter adapter;
    private SavingDao savingDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        emptyState = findViewById(R.id.emptyState);

        // Initialize database
        savingDao = AppDatabase.getInstance(this).savingDao();

        // Get currency preference
        SharedPreferences prefs = getSharedPreferences("DailySaverPrefs", MODE_PRIVATE);
        String currencyCode = prefs.getString("currency", 
            Currency.getInstance(Locale.getDefault()).getCurrencyCode());

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SavingEntryAdapter(this, currencyCode);
        recyclerView.setAdapter(adapter);

        // Set up swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            
            @Override
            public boolean onMove(RecyclerView recyclerView, 
                RecyclerView.ViewHolder viewHolder, 
                RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                SavingEntry deletedEntry = adapter.getEntryAt(position);
                
                // Delete entry
                new Thread(() -> {
                    savingDao.deleteEntry(deletedEntry);
                    runOnUiThread(() -> {
                        loadEntries();
                        showUndoSnackbar(deletedEntry);
                    });
                }).start();
            }
        }).attachToRecyclerView(recyclerView);

        // Load entries
        loadEntries();
    }

    private void loadEntries() {
        new Thread(() -> {
            List<SavingEntry> entries = savingDao.getAllEntries();
            runOnUiThread(() -> {
                adapter.setEntries(entries);
                updateEmptyState(entries.isEmpty());
            });
        }).start();
    }

    private void updateEmptyState(boolean isEmpty) {
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showUndoSnackbar(SavingEntry deletedEntry) {
        Snackbar.make(recyclerView, R.string.entry_deleted, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo, v -> {
                new Thread(() -> {
                    savingDao.insertEntry(deletedEntry);
                    runOnUiThread(this::loadEntries);
                }).start();
            })
            .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 