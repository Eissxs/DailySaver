package com.Eissxs.dailysaver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.Eissxs.dailysaver.R;
import com.Eissxs.dailysaver.database.SavingEntry;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class SavingEntryAdapter extends RecyclerView.Adapter<SavingEntryAdapter.ViewHolder> {
    private List<SavingEntry> entries = new ArrayList<>();
    private final Context context;
    private final String currencyCode;
    private final SimpleDateFormat dateFormat;
    private final NumberFormat currencyFormat;

    public SavingEntryAdapter(Context context, String currencyCode) {
        this.context = context;
        this.currencyCode = currencyCode;
        this.dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        this.currencyFormat = NumberFormat.getCurrencyInstance();
        this.currencyFormat.setCurrency(Currency.getInstance(currencyCode));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saving_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavingEntry entry = entries.get(position);
        
        // Format amount with currency
        holder.amountText.setText(currencyFormat.format(entry.amount));
        
        // Set note if available
        if (entry.note != null && !entry.note.isEmpty()) {
            holder.noteText.setVisibility(View.VISIBLE);
            holder.noteText.setText(entry.note);
        } else {
            holder.noteText.setVisibility(View.GONE);
        }
        
        // Format date
        holder.dateText.setText(dateFormat.format(entry.date));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void setEntries(List<SavingEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    public SavingEntry getEntryAt(int position) {
        return entries.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView amountText;
        final TextView noteText;
        final TextView dateText;

        ViewHolder(View itemView) {
            super(itemView);
            amountText = itemView.findViewById(R.id.amountText);
            noteText = itemView.findViewById(R.id.noteText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }
} 