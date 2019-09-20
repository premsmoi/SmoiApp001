package com.example.smoiapp001.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smoiapp001.R
import com.example.smoiapp001.models.Currency
import kotlinx.android.synthetic.main.currency_layout.view.*
import timber.log.Timber

class CurrencyAdapter(private val mContext: Context?) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private var currencies: ArrayList<Currency> = ArrayList<Currency>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view = LayoutInflater.from(mContext).
                inflate(R.layout.currency_layout, parent, false)

        return CurrencyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return currencies.size
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        Timber.i("position: "+position)
        val currency =  currencies.get(position)
        val label = currency.label
        holder.label.text = label
        holder.flagIcon.setImageResource(getFlagIconSource(label))
        val rateString = String.format("%.4f THB", (1/currency.rate))
        holder.rate.text = rateString
    }

    fun setCurrencies(currencies: ArrayList<Currency>) {
        this.currencies = currencies
        notifyDataSetChanged()
        Timber.i("My currencies :"+this.currencies.toString())
    }

    private fun getFlagIconSource(label: String)
    = when(label) {
        "CNY" -> R.mipmap.ic_china_foreground
        "EUR" -> R.mipmap.ic_euro_foreground
        "INR" -> R.mipmap.ic_india_foreground
        "JPY" -> R.mipmap.ic_japan_foreground
        "KRW" -> R.mipmap.ic_korea_foreground
        "GBP" -> R.mipmap.ic_uk_foreground
        "USD" -> R.mipmap.ic_usa_foreground
        else -> R.mipmap.ic_usa_foreground
    }

    // Inner class for creating ViewHolders
    inner class CurrencyViewHolder
    /**
     * Constructor for the TaskViewHolders.
     *
     * @param itemView The view inflated in onCreateViewHolder
     */
    (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val flagIcon: ImageView = itemView.flagIcon
        val label: TextView = itemView.currencyLabel
        val rate: TextView = itemView.currencyRate

    }
}