package com.example.smoiapp001.loaders;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

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
        return db.transactionDao().loadRecommendedCostByDescription(keyword);
    }

}
