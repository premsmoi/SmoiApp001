package com.example.smoiapp001.utilities;

import com.example.smoiapp001.database.models.TransactionEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionUtils {

    // Constant for editing transaction period, 1 day
    private static final long EDITABLE_TRANSACTION_PERIOD = DateUtils.DAY_IN_MILLISECOND;

    public static ArrayList<String> getDescriptionList(List<TransactionEntry> transactions) {
        ArrayList<String> descriptions = new ArrayList<>();
        for (TransactionEntry transaction : transactions) {
            String newDescription = transaction.getDescription();
            if (!descriptions.contains(newDescription)) {
                descriptions.add(newDescription);
            }
        }
        return descriptions;
    }

    public static ArrayList<TransactionEntry> getTransactionByDate(List<TransactionEntry> transactions,
                                                                   Calendar calendar) {
        ArrayList<TransactionEntry> selectedTransactions = new ArrayList<>();
        for (TransactionEntry transaction : transactions) {
            Calendar entryCalendar = Calendar.getInstance();
            entryCalendar.setTime(transaction.getDate());

            if (calendar.get(Calendar.DAY_OF_YEAR) == entryCalendar.get(Calendar.DAY_OF_YEAR)) {
                selectedTransactions.add(transaction);
            }
        }
        return selectedTransactions;
    }

    public static float calculateDayCost(List<TransactionEntry> transactions,
                                         Calendar calendar) {
        float daySum = 0;
        for (TransactionEntry transaction : transactions) {
            Calendar entryCalendar = Calendar.getInstance();
            entryCalendar.setTime(transaction.getDate());

            if (calendar.get(Calendar.YEAR) == entryCalendar.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == entryCalendar.get(Calendar.MONTH) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == entryCalendar.get(Calendar.DAY_OF_MONTH)) {
                daySum += transaction.getCost();
            }

        }
        return daySum;
    }

    public static float calculateMonthCost(List<TransactionEntry> transactions,
                                         Calendar calendar) {
        float monthSum = 0;
        for (TransactionEntry transaction : transactions) {
            Calendar entryCalendar = Calendar.getInstance();
            entryCalendar.setTime(transaction.getDate());

            if (calendar.get(Calendar.YEAR) == entryCalendar.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == entryCalendar.get(Calendar.MONTH)) {
                monthSum += transaction.getCost();
            }

        }
        return monthSum;
    }

    public static float getCostModeByDescription(String description) {

        return 0;
    }

    public static String[] getDescriptionSearchResult(String description) {

        return null;
    }

    public static boolean isLocked(TransactionEntry transaction) {
        long nowTimestamp = DateUtils.toTimestamp(new Date());
        long transactionTimestamp = DateUtils.toTimestamp(transaction.getDate());
        if (nowTimestamp - transactionTimestamp >= EDITABLE_TRANSACTION_PERIOD) {
            return true;
        }
        return false;
    }
}
