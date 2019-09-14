package com.example.smoiapp001.utilities

import androidx.lifecycle.ViewModel
import com.example.smoiapp001.viewmodels.MainViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import android.R.attr.mode
import android.R.attr.name
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.android.gms.common.util.SharedPreferencesUtils
import android.R.id.edit
import java.util.*


const val PREFERENCE_NAME = "my_preferances"


object FirebaseUtils {

    fun syncToFirebaseDatabase(viewModel: MainViewModel, context: Context) {
        val firebaseDB = FirebaseDatabase.getInstance().reference
        val transactionEntries = viewModel.transactions.getValue()
        val sp =  context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)
        val editor = sp.edit()
        /*editor.putLong("lastSyncTime", 1567768906967);
        editor.commit();*/
        val lastSyncTime = sp.getLong("lastSyncTime", 0)
        Timber.i("lastSyncTime is "+lastSyncTime)
        /*Timber.i("Now is "+DateUtils.getCurrectTimeStamp())*/

        for (transaction in transactionEntries!!) {
            val transactionDate = DateUtils.toTimestamp(transaction.date!!)
            if (transactionDate <= lastSyncTime) {
                continue
            }

            if (!TransactionUtils.isLocked(transaction)) {
                continue
            } else {
                /*Timber.i("It's locked!");*/
            }
            //Timber.i(transactionDate.toString())
            //Timber.i("Item is ready to be synced")

            val transactionObj = transaction.toMap()
            val transactionsRef = firebaseDB.child("transactions")
            transactionsRef.orderByChild("id").equalTo(transaction.id.toDouble()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //Log.i(TAG, "exists!!! No need to add");
                    } else {
                        transactionsRef.push().setValue(transactionObj)
                        editor.putLong("lastSyncTime", DateUtils.getCurrentTimeStamp());
                        editor.commit();
                        Timber.i("sync %s to Firebase", transaction.description);
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }

}