/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.smoiapp001;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smoiapp001.models.TransactionEntry;
import com.example.smoiapp001.utilities.DateConverter;

import java.util.List;

/**
 * This TaskAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<TransactionEntry> mTransactionEntries;
    private Context mContext;


    /**
     * Constructor for the TaskAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public TransactionAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.transaction_layout, parent, false);

        return new TransactionViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        // Determine the values of the wanted data
        TransactionEntry transactionEntry = mTransactionEntries.get(position);
        String description = transactionEntry.getDescription();
        float cost = transactionEntry.getCost();
        String costString = Float.toString(cost);
        int costTextColor;
        String updatedAt = DateConverter.getNormalDateFormat().format(transactionEntry.getDate());

        //Set values
        holder.descriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);

        if (cost >= 0) {
            costString = "+" + costString;
            costTextColor = R.color.colorGreen;
        } else {
            costTextColor = R.color.colorRed;
        }
        holder.costView.setTextColor(ContextCompat.getColor(mContext, costTextColor));
        holder.costView.setText(costString);
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mTransactionEntries == null) {
            return 0;
        }
        return mTransactionEntries.size();
    }

    public List<TransactionEntry> getTransactions() {
        return mTransactionEntries;
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setTransactions(List<TransactionEntry> transactionsEntries) {
        mTransactionEntries = transactionsEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class TransactionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView descriptionView;
        TextView updatedAtView;
        TextView costView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TransactionViewHolder(View itemView) {
            super(itemView);

            descriptionView = itemView.findViewById(R.id.tv_transaction_description);
            updatedAtView = itemView.findViewById(R.id.tv_transaction_updated_at);
            costView = itemView.findViewById(R.id.tv_transaction_cost);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mTransactionEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}