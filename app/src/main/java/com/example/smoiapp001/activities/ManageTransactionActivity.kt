/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.smoiapp001.activities

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter


import com.example.smoiapp001.utilities.AppExecutors
import com.example.smoiapp001.loaders.LoadPopularCost
import com.example.smoiapp001.loaders.LoadTransactionById
import com.example.smoiapp001.R
import com.example.smoiapp001.database.AppDatabase
import com.example.smoiapp001.database.models.TransactionEntry
import com.example.smoiapp001.utilities.DateUtils
import com.example.smoiapp001.utilities.TransactionUtils
import kotlinx.android.synthetic.main.activity_manage_transaction.*
import timber.log.Timber

import java.util.ArrayList
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue


class ManageTransactionActivity : AppCompatActivity() {

    private var mTransaction: TransactionEntry? = null

    // Member variable for the Database
    private lateinit var mDb: AppDatabase

    private var descriptionList = ArrayList<String>()

    private var mTransactionId = DEFAULT_TRANSACTION_ID

    private val isDrafted: Boolean
        get() {
            if (descriptionInput.text.toString() != "" && costInput.text.toString() != "") {
                return true
            }
            return false
        }

    private val transactionTypeFromViews: String
        get() = when (radioGroup.checkedRadioButtonId) {
                R.id.radButtonExpense -> EXPENSE_TRANSACTION_TYPE
                R.id.radButtonIncome -> INCOME_TRANSACTION_TYPE
                else -> EXPENSE_TRANSACTION_TYPE
            }

    private val transactionLoaderListener = object : LoaderManager.LoaderCallbacks<TransactionEntry> {
        override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<TransactionEntry> {
            return LoadTransactionById(this@ManageTransactionActivity, mDb, mTransactionId)
        }

        override fun onLoadFinished(loader: Loader<TransactionEntry>, transactionEntry: TransactionEntry) {
            mTransaction = transactionEntry
            populateUI(mTransaction)
            if (TransactionUtils.isLocked(mTransaction!!)) {
                lockTransaction()
            }
            manageTransactionLayout.visibility = View.VISIBLE
        }

        override fun onLoaderReset(loader: Loader<TransactionEntry>) {
            mTransaction = null
        }
    }

    private val popularCostLoaderListener = object : LoaderManager.LoaderCallbacks<Float> {
        override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<Float> {
            var keyword = "???"
            if (bundle != null && bundle.containsKey(EXTRA_DESCRIPTION_KEYWORD)) {
                keyword = bundle.getString(EXTRA_DESCRIPTION_KEYWORD)!!
            }
            return LoadPopularCost(this@ManageTransactionActivity, mDb, keyword)
        }

        override fun onLoadFinished(loader: Loader<Float>, popularCost: Float) {
            costInput.setText(String.format(Locale.US, "%.2f", popularCost.absoluteValue))
        }

        override fun onLoaderReset(loader: Loader<Float>) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_transaction)
        initViews()

        mDb = AppDatabase.getInstance(applicationContext)
        getDescriptions()

        val itemId = ManageTransactionActivityArgs.fromBundle(intent.extras!!).itemId
        Timber.i("itemId after navigate is %d",itemId)
        if (intent != null) {
            if (itemId != -1) {
                manageTransactionLayout.visibility = View.INVISIBLE
                this.setTitle(R.string.update_transaction_activity_name)
                deleteButton.visibility = View.VISIBLE
                saveButton.isEnabled = true
                saveButton.setText(R.string.update_button)
                loadTransactionById(itemId)
            }
        }

        val arrayAdapter = ArrayAdapter(this,
                android.R.layout.simple_dropdown_item_1line, descriptionList)
        descriptionInput.setAdapter(arrayAdapter)
    }

    private fun getDescriptions() {
        AppExecutors.instance.diskIO().execute {
            for (description in mDb.transactionDao().allDescriptions) {
                /*Timber.i(description)*/
                descriptionList.add(description)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(INSTANCE_TRANSACTION_ID, mTransactionId)
        super.onSaveInstanceState(outState)
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private fun initViews() {
        saveButton.isEnabled = false
        deleteButton.visibility = View.INVISIBLE

        costInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                saveButton.isEnabled = isDrafted
            }
        })

        descriptionInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                saveButton.isEnabled = isDrafted
                costInput.text = null
            }
        })

        descriptionInput.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            loadRecommendedCost()
        }

        saveButton.setOnClickListener { onSaveButtonClicked() }

        deleteButton.setOnClickListener { onDeleteButtonClicked() }
    }

    private fun lockTransaction() {
        var costString = costInput.text.toString()
        costString += " THB"
        costInput.setText(costString)
        descriptionInput.isFocusable = false
        descriptionInput.setBackgroundColor(Color.TRANSPARENT)
        costInput.isFocusable = false
        costInput.setBackgroundColor(Color.TRANSPARENT)
        saveButton.visibility = View.INVISIBLE
        deleteButton.visibility = View.INVISIBLE

        val checkedIndex = radioGroup.indexOfChild(
                radioGroup.findViewById(
                        radioGroup.checkedRadioButtonId))
        //Log.i(TAG, "checkedIndex: "+checkedIndex);
        for (i in 0 until radioGroup.childCount) {
            if (i != checkedIndex) {
                radioGroup.getChildAt(i).isEnabled = false
            }
        }
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param transaction the taskEntry to populate the UI
     */
    private fun populateUI(transaction: TransactionEntry?) {
        if (transaction == null) {
            return
        }
        descriptionInput.setText(transaction.description)
        costInput.setText(String.format(Locale.US, "%.2f", transaction.cost.absoluteValue))
        date.text = DateUtils.getObviousDateFormat().format(transaction.date)

        var type = EXPENSE_TRANSACTION_TYPE
        if (transaction.cost >= 0) {
            type = INCOME_TRANSACTION_TYPE
        }
        setTransactionTypeInViews(type)
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    private fun onSaveButtonClicked() {
        val description = descriptionInput.text.toString()
        var cost = java.lang.Float.parseFloat(costInput.text.toString())
        val date = Date()
        val type = transactionTypeFromViews

        if (type == EXPENSE_TRANSACTION_TYPE) {
            cost *= -1f
        }

        val transaction = TransactionEntry(description, cost, date)
        AppExecutors.instance.diskIO().execute {
            if (mTransactionId == DEFAULT_TRANSACTION_ID) {
                // insert new mTransaction
                mDb.transactionDao().insertTransaction(transaction)
            } else {
                //update mTransaction
                transaction.id = mTransactionId
                mDb.transactionDao().updateTransaction(transaction)
            }
            finish()
        }
    }

    private fun onDeleteButtonClicked() {
        AppExecutors.instance.diskIO().execute {
            if (mTransactionId != DEFAULT_TRANSACTION_ID) {
                // delete selected mTransaction
                mDb.transactionDao().deleteTransactionById(mTransactionId)
            }
            finish()
        }
    }

    private fun setTransactionTypeInViews(type: String) {
        when (type) {
            EXPENSE_TRANSACTION_TYPE -> radioGroup.check(R.id.radButtonExpense)
            INCOME_TRANSACTION_TYPE -> radioGroup.check(R.id.radButtonIncome)
            else -> radioGroup.check(R.id.radButtonExpense)
        }
    }

    private fun loadTransactionById(itemId: Int) {
        if (mTransactionId == DEFAULT_TRANSACTION_ID) {
            mTransactionId = itemId
            LoaderManager.getInstance(this).initLoader(TRANSACTION_LOADER_ID, null, transactionLoaderListener)
        }
    }

    private fun loadRecommendedCost() {
        var keyword = descriptionInput.text.toString()
        keyword = "%$keyword%"
        val bundle = Bundle()
        bundle.putString(EXTRA_DESCRIPTION_KEYWORD, keyword)
        LoaderManager.getInstance(this).restartLoader(POPULAR_COST_LOADER_ID,
                bundle, popularCostLoaderListener)
    }

    companion object {

        // Constants for mTransaction type
        private const val EXPENSE_TRANSACTION_TYPE = "expense"
        private const val INCOME_TRANSACTION_TYPE = "income"

        private const val TRANSACTION_LOADER_ID = 21
        private const val POPULAR_COST_LOADER_ID = 22

        // Extra for the mTransaction ID to be received after rotation
        const val INSTANCE_TRANSACTION_ID = "instanceTransactionId"
        const val EXTRA_DESCRIPTION_KEYWORD = "description-keyword"

        const val ACTION_MANAGE_TRANSACTION = "manage-transaction"

        // Constant for default task id to be used when not in update mode
        private const val DEFAULT_TRANSACTION_ID = -1
    }

}
