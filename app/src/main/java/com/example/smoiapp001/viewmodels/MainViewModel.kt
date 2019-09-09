package com.example.smoiapp001.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import com.example.smoiapp001.database.AppDatabase
import com.example.smoiapp001.database.models.TransactionEntry

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val transactions: LiveData<List<TransactionEntry>>

    init {
        val database = AppDatabase.getInstance(this.getApplication())
        transactions = database.transactionDao().loadAllTransactions()
    }

}
