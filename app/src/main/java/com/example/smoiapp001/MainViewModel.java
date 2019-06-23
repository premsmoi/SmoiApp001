package com.example.smoiapp001;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.smoiapp001.models.TransactionEntry;
import com.example.smoiapp001.database.AppDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<TransactionEntry>> transactions;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        transactions = database.transactionDao().loadAllTransactions();
    }

    public LiveData<List<TransactionEntry>> getTasks() {
        return transactions;
    }
}
