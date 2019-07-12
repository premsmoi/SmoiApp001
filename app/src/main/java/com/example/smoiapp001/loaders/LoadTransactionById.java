package com.example.smoiapp001.loaders;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.smoiapp001.database.AppDatabase;
import com.example.smoiapp001.database.models.TransactionEntry;

public class LoadTransactionById extends AsyncTaskLoader<TransactionEntry> {
    private AppDatabase db;
    private Integer id;

    public LoadTransactionById(@NonNull Context context, AppDatabase db, Integer id ) {
        super(context);
        this.db = db;
        this.id = id;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public TransactionEntry loadInBackground() {
        return db.transactionDao().loadTransactionById(id);
    }

}
