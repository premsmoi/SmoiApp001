package com.example.smoiapp001.adapters

import android.content.Context
import android.database.Cursor
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.smoiapp001.R
import com.example.smoiapp001.fragments.DashboardFragment
import com.example.smoiapp001.database.AppDatabase
import kotlinx.android.synthetic.main.ranking_transaction_layout.view.*

import java.util.Locale

class RankingTransactionAdapter(private val mContext: Context?) : RecyclerView.Adapter<RankingTransactionAdapter.ReportedTransactionViewHolder>() {

    private var items: Cursor? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportedTransactionViewHolder {
        val view = LayoutInflater.from(mContext)
                .inflate(R.layout.ranking_transaction_layout, parent, false)

        return ReportedTransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportedTransactionViewHolder, position: Int) {
        items!!.moveToPosition(position)
        var name = ""
        var value: Int? = 0

        name = items!!.getString(AppDatabase.RANK_NAME_COL_IDX)
        value = items!!.getInt(AppDatabase.RANK_VALUE_COL_IDX)

        if (value < 0) {
            value = Math.abs(value)
            holder.sumView.setTextColor(ContextCompat.getColor(mContext!!, R.color.colorRed))
        } else {
            holder.sumView.setTextAppearance(R.style.TextAppearance_AppCompat_Medium)
        }

        val valueString = String.format(Locale.US, "%d", value)

        holder.descriptionView.text = name
        holder.sumView.text = valueString
    }

    override fun getItemCount(): Int {
        return DashboardFragment.TOP_NUMBER
    }

    fun setItems(items: Cursor) {
        this.items = items
        notifyDataSetChanged()
    }

    // Inner class for creating ViewHolders
    inner class ReportedTransactionViewHolder
    /**
     * Constructor for the TaskViewHolders.
     *
     * @param itemView The view inflated in onCreateViewHolder
     */
    (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val descriptionView: TextView = itemView.transactionDescription
        val sumView: TextView = itemView.transactionSum

    }

}
