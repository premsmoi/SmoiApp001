package com.example.smoiapp001.utilities

import com.example.smoiapp001.database.models.TransactionEntry
import java.util.ArrayList
import java.util.Calendar
import java.util.Date

object TransactionUtils {

    // Constant for editing transaction period, 1 day
    private val EDITABLE_TRANSACTION_PERIOD = DateUtils.DAY_IN_MILLISECOND

    fun getDescriptionList(transactions: List<TransactionEntry>): ArrayList<String> {
        val descriptions = ArrayList<String>()
        for (transaction in transactions) {
            val newDescription = transaction.description
            if (!descriptions.contains(newDescription)) {
                descriptions.add(newDescription!!)
            }
        }
        return descriptions
    }

    fun getTransactionByDate(transactions: List<TransactionEntry>,
                             calendar: Calendar): ArrayList<TransactionEntry> {
        val selectedTransactions = ArrayList<TransactionEntry>()
        for (transaction in transactions) {
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = transaction.date

            if (calendar.get(Calendar.DAY_OF_YEAR) == entryCalendar.get(Calendar.DAY_OF_YEAR)) {
                selectedTransactions.add(transaction)
            }
        }
        return selectedTransactions
    }

    fun calculateDayCost(transactions: List<TransactionEntry>,
                         calendar: Calendar): Float {
        var daySum = 0f
        for (transaction in transactions) {
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = transaction.date

            if (calendar.get(Calendar.YEAR) == entryCalendar.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == entryCalendar.get(Calendar.MONTH) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == entryCalendar.get(Calendar.DAY_OF_MONTH)) {
                daySum += transaction.cost
            }

        }
        return daySum
    }

    fun calculateMonthCost(transactions: List<TransactionEntry>,
                           calendar: Calendar): Float {
        var monthSum = 0f
        for (transaction in transactions) {
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = transaction.date

            if (calendar.get(Calendar.YEAR) == entryCalendar.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == entryCalendar.get(Calendar.MONTH)) {
                monthSum += transaction.cost
            }

        }
        return monthSum
    }

    fun getCostModeByDescription(description: String): Float {

        return 0f
    }

    fun getDescriptionSearchResult(description: String): Array<String>? {

        return null
    }

    fun isLocked(transaction: TransactionEntry): Boolean {
        val nowTimestamp = DateUtils.toTimestamp(Date())
        val transactionTimestamp = DateUtils.toTimestamp(transaction.date!!)
        return nowTimestamp - transactionTimestamp >= EDITABLE_TRANSACTION_PERIOD
    }
}
