package com.example.smoiapp001.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.smoiapp001.Models.TransactionEntry;

import java.util.List;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM `transaction` ORDER BY date")
    LiveData<List<TransactionEntry>> loadAllTransactions();

    @Insert
    void insertTransaction(TransactionEntry transactionEntry);

    @Update
    void updateTransaction(TransactionEntry transactionEntry);

    @Delete
    void deleteTransaction(TransactionEntry transactionEntry);

    @Query("SELECT * FROM `transaction` WHERE id = :id")
    LiveData<TransactionEntry> loadTransactionById(int id);
}
