package com.example.smoiapp001.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.smoiapp001.MyApplication
import com.example.smoiapp001.database.AppDatabase
import com.example.smoiapp001.database.models.TransactionEntry
import javax.inject.Inject

class MainViewModel() : ViewModel() {

    @Inject
    lateinit var database: AppDatabase
    val transactions: LiveData<List<TransactionEntry>>

    init {
        MyApplication.mAppComponent.inject(this)
        transactions = database.transactionDao().loadAllTransactions()
    }

}
