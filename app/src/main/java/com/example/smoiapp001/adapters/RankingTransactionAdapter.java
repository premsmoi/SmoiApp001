package com.example.smoiapp001.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smoiapp001.R;
import com.example.smoiapp001.activities.MainActivity;
import com.example.smoiapp001.activities.fragments.DashboardFragment;
import com.example.smoiapp001.database.AppDatabase;

import java.util.Locale;

public class RankingTransactionAdapter extends RecyclerView.Adapter<RankingTransactionAdapter.ReportedTransactionViewHolder> {

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();

    private Context mContext;

    private Cursor items;

    public RankingTransactionAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ReportedTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.ranking_transaction_layout, parent, false);

        return new ReportedTransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportedTransactionViewHolder holder, int position) {
        items.moveToPosition(position);
        /*Log.i("column count", ""+items.getColumnCount());
        Log.i("row count", ""+items.getCount());*/
        String name = items.getString(AppDatabase.RANK_NAME_COL_IDX);

        Integer value = items.getInt(AppDatabase.RANK_VALUE_COL_IDX);

        if (value < 0) {
            value = Math.abs(value);
            holder.sumView.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
        }
        else {
            holder.sumView.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        }

        String valueString = String.format(Locale.US,"%d", value);

        holder.descriptionView.setText(name);
        holder.sumView.setText(valueString);
    }

    @Override
    public int getItemCount() {
        return DashboardFragment.TOP_NUMBER;
    }

    public void setItems(Cursor items) {
        this.items = items;
        notifyDataSetChanged();
    }

    // Inner class for creating ViewHolders
    class ReportedTransactionViewHolder extends RecyclerView.ViewHolder {

        TextView descriptionView;
        TextView sumView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public ReportedTransactionViewHolder(View itemView) {
            super(itemView);

            descriptionView = itemView.findViewById(R.id.tv_transaction_description);
            sumView = itemView.findViewById(R.id.tv_transaction_sum);
        }

    }
}
