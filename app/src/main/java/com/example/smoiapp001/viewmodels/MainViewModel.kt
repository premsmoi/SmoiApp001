package com.example.smoiapp001.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import android.util.Log

import com.example.smoiapp001.database.models.TransactionEntry
import com.example.smoiapp001.database.AppDatabase

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val transactions: LiveData<List<TransactionEntry>>

    init {
        val database = AppDatabase.getInstance(this.getApplication())
        transactions = database.transactionDao().loadAllTransactions()
    }

}
