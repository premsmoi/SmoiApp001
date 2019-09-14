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

package com.example.smoiapp001.adapters

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.smoiapp001.R
import com.example.smoiapp001.database.models.TransactionEntry
import com.example.smoiapp001.utilities.DateUtils
import kotlinx.android.synthetic.main.transaction_layout.view.*
import java.util.Locale

/**
 * This TaskAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
class TransactionAdapter
/**
 * Constructor for the TaskAdapter that initializes the Context.
 *
 * @param mContext  the current Context
 * @param mItemClickListener the ItemClickListener
 */
(private val mContext: Context, // Member variable to handle item clicks
 private val mItemClickListener: ItemClickListener) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    // Class variables for the List that holds task data and the Context
    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    var transactions: List<TransactionEntry>? = null
        set(transactionsEntries) {
            field = transactionsEntries
            notifyDataSetChanged()
        }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        // Inflate the task_layout to a view
        val view = LayoutInflater.from(mContext)
                .inflate(R.layout.transaction_layout, parent, false)

        return TransactionViewHolder(view)
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        // Determine the values of the wanted data
        val transactionEntry = transactions!![position]
        val description = transactionEntry.description
        val cost = transactionEntry.cost
        var costString = String.format(Locale.US, "%.2f", cost)
        val costTextColor: Int
        val updatedAt = DateUtils.getNormalDateFormat().format(transactionEntry.date)

        //Set values
        holder.descriptionView.text = description
        holder.updatedAtView.text = updatedAt

        if (cost >= 0) {
            costString = "+$costString"
            costTextColor = R.color.colorGreen
        } else {
            costTextColor = R.color.colorRed
        }
        holder.costView.setTextColor(ContextCompat.getColor(mContext, costTextColor))
        holder.costView.text = costString
    }

    /**
     * Returns the number of items to display.
     */
    override fun getItemCount(): Int {
        return if (transactions == null) {
            0
        } else transactions!!.size
    }

    interface ItemClickListener {
        fun onItemClickListener(itemId: Int)
    }

    // Inner class for creating ViewHolders
    inner class TransactionViewHolder
    /**
     * Constructor for the TaskViewHolders.
     *
     * @param itemView The view inflated in onCreateViewHolder
     */
    (itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        // Class variables for the task description and priority TextViews
        var descriptionView: TextView = itemView.transactionDescription
        var updatedAtView: TextView = itemView.transactionDate
        var costView: TextView = itemView.transactionCost

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val elementId = transactions!![adapterPosition].id
            mItemClickListener.onItemClickListener(elementId)
        }
    }
}