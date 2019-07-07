package com.example.smoiapp001.activities.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.example.smoiapp001.database.AppDatabase;

public class LoadPopularCost extends AsyncTaskLoader<Float> {
    private AppDatabase db;
    private String keyword;

    public LoadPopularCost(@NonNull Context context, AppDatabase db, String keyword ) {
        super(context);
        this.db = db;
        this.keyword = keyword;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public Float loadInBackground() {
        return db.transactionDao().loadPopularCostByDescription(keyword);
    }

}
