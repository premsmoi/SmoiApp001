package com.example.smoiapp001.loaders

import android.content.Context
import androidx.loader.content.AsyncTaskLoader

import com.example.smoiapp001.database.AppDatabase

class LoadPopularCost(context: Context, private val db: AppDatabase, private val keyword: String) : AsyncTaskLoader<Float>(context) {

    override fun onStartLoading() {
        super.onStartLoading()
        forceLoad()
    }

    override fun loadInBackground(): Float? {
        return db.transactionDao().loadRecommendedCostByDescription(keyword)
    }

}
