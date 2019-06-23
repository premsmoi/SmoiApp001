package com.example.smoiapp001;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.smoiapp001.models.TransactionEntry;
import com.example.smoiapp001.database.AppDatabase;

public class ManageTransactionViewModel extends ViewModel {

    private LiveData<TransactionEntry> transaction;

    public ManageTransactionViewModel(AppDatabase database, int transactionId) {
        transaction = database.transactionDao().loadTransactionById(transactionId);
    }

    public LiveData<TransactionEntry> getTransaction() {
        return transaction;
    }
}
