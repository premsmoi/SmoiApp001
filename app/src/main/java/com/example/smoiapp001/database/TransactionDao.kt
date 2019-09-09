package com.example.smoiapp001.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.smoiapp001.database.models.TransactionEntry

@Dao
interface TransactionDao {

    @get:Query("SELECT DISTINCT description FROM `transaction`")
    val allDescriptions: List<String>

    @Query("SELECT * FROM `transaction` ORDER BY date DESC")
    fun loadAllTransactions(): LiveData<List<TransactionEntry>>

    @Insert
    fun insertTransaction(transactionEntry: TransactionEntry)

    @Update
    fun updateTransaction(transactionEntry: TransactionEntry)

    @Query("DELETE FROM `transaction` WHERE id = :id")
    fun deleteTransactionById(id: Int)

    @Query("SELECT * FROM `transaction` WHERE id = :id")
    fun loadTransactionById(id: Int): TransactionEntry

    @Query("SELECT cost FROM `transaction` " +
            "WHERE description LIKE :keyword GROUP BY cost " +
            "ORDER BY COUNT(cost) DESC LIMIT 1")
    fun loadRecommendedCostByDescription(keyword: String): Float?


}
