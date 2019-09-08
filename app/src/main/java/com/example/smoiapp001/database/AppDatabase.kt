package com.example.smoiapp001.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import android.database.Cursor
import android.util.Log

import com.example.smoiapp001.database.models.TransactionEntry
import com.example.smoiapp001.utilities.DateUtils
import timber.log.Timber

@Database(entities = [TransactionEntry::class], version = 2, exportSchema = false)
@TypeConverters(DateUtils::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {

        private val LOG_TAG = AppDatabase::class.java.simpleName
        private val LOCK = Any()
        private val DATABASE_NAME = "smoi_app_001"
        @Volatile private var sInstance: AppDatabase? = null

        val SUM_RECORD_COL_NAME = "sum_record"
        val SUM_COST_COL_NAME = "sum_cost"
        val RANK_NAME_COL_IDX = 0
        val RANK_VALUE_COL_IDX = 1

        fun getInstance(context: Context): AppDatabase {
            if (sInstance == null) {
                synchronized(LOCK) {
                    Timber.i("Creating new database instance")
                    sInstance = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            Timber.i("Getting the database instance")
            val currentDBPath = sInstance!!.openHelper.writableDatabase.path
            return sInstance!!
        }

        fun loadMostRecordedItems(number: Int?, sinceDate: Long): Cursor {
            var dateCondition = ""
            if (sinceDate != 0L) {
                dateCondition = "WHERE date >= $sinceDate"
            }
            val queryString = "SELECT description, COUNT(description) " +
                    "AS " + SUM_RECORD_COL_NAME + " FROM `transaction` " +
                    dateCondition + " GROUP BY description " +
                    "ORDER BY " + SUM_RECORD_COL_NAME + " DESC LIMIT " + number

            return sInstance!!.query(queryString, null)
        }

        fun loadMostCostItems(number: Int?, sinceDate: Long): Cursor {
            var dateCondition = ""
            if (sinceDate != 0L) {
                dateCondition = "WHERE date >= $sinceDate"
            }
            val queryString = "SELECT description, CAST(SUM(cost) AS Integer) " + SUM_COST_COL_NAME +
                    " FROM `transaction` " + dateCondition +
                    " GROUP BY description" +
                    " ORDER BY " + SUM_COST_COL_NAME + " ASC LIMIT " + number

            return sInstance!!.query(queryString, null)
        }
    }


}
