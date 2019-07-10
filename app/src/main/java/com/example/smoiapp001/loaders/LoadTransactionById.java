package com.example.smoiapp001.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

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
