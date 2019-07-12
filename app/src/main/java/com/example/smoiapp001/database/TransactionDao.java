package com.example.smoiapp001.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smoiapp001.database.models.TransactionEntry;

import java.util.List;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM `transaction` ORDER BY date DESC")
    LiveData<List<TransactionEntry>> loadAllTransactions();

    @Insert
    void insertTransaction(TransactionEntry transactionEntry);

    @Update
    void updateTransaction(TransactionEntry transactionEntry);

    @Query("DELETE FROM `transaction` WHERE id = :id")
    void deleteTransactionById(int id);

    @Query("SELECT * FROM `transaction` WHERE id = :id")
    TransactionEntry loadTransactionById(int id);

    @Query("SELECT cost FROM `transaction` " +
            "WHERE description LIKE :keyword GROUP BY cost " +
            "ORDER BY COUNT(cost) DESC LIMIT 1")
    Float loadRecommendedCostByDescription(String keyword);


}
