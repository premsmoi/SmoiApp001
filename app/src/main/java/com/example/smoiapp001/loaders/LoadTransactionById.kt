package com.example.smoiapp001.loaders

import android.content.Context
import androidx.loader.content.AsyncTaskLoader

import com.example.smoiapp001.database.AppDatabase
import com.example.smoiapp001.database.models.TransactionEntry

class LoadTransactionById(
        context: Context,
        private val db: AppDatabase,
        private val id: Int?) : AsyncTaskLoader<TransactionEntry>(context) {

    override fun onStartLoading() {
        super.onStartLoading()
        forceLoad()
    }

    override fun loadInBackground(): TransactionEntry? {
        return db.transactionDao().loadTransactionById(id!!)
    }

}
