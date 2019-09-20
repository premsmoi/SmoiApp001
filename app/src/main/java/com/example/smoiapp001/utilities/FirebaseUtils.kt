package com.example.smoiapp001.utilities

import android.content.Context
import com.example.smoiapp001.viewmodels.MainViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber


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
        Timber.i("lastSyncTime is %s", lastSyncTime.toString())
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