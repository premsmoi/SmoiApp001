package com.example.smoiapp001.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQueryBuilder;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.smoiapp001.database.models.TransactionEntry;
import com.example.smoiapp001.utilities.DateConverter;

import java.util.Date;

@Database(entities = {TransactionEntry.class}, version = 2, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "smoi_app_001";
    private static AppDatabase sInstance;

    public static final String SUM_RECORD_COL_NAME = "sum_record";
    public static final String SUM_COST_COL_NAME = "sum_cost";
    public static final Integer RANK_NAME_COL_IDX = 0;
    public static final Integer RANK_VALUE_COL_IDX = 1;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        String currentDBPath = sInstance.getOpenHelper().getWritableDatabase().getPath();
        Log.i("DB PATH",currentDBPath);
        return sInstance;
    }

    public abstract TransactionDao transactionDao();

    public static Cursor loadMostRecordedItems(Integer number, long sinceDate) {
        String dateCondition = "";
        if (sinceDate != 0) {
            dateCondition = "WHERE date >= " + sinceDate;
        }
        String queryString =
                "SELECT description, COUNT(description) " +
                "AS " + SUM_RECORD_COL_NAME + " FROM `transaction` " +
                dateCondition + " GROUP BY description " +
                "ORDER BY "+SUM_RECORD_COL_NAME + " DESC LIMIT "+number;

        return sInstance.query(queryString, null);
    }

    public static Cursor loadMostCostItems(Integer number, long sinceDate) {
        String dateCondition = "";
        if (sinceDate != 0) {
            dateCondition = "WHERE date >= " + sinceDate;
        }
        String queryString =
                "SELECT description, CAST(SUM(cost) AS Integer) "+SUM_COST_COL_NAME +
                        " FROM `transaction` " + dateCondition +
                        " GROUP BY description" +
                        " ORDER BY " + SUM_COST_COL_NAME + " ASC LIMIT "+number;

        return sInstance.query(queryString, null);
    }


}
