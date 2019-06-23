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

package com.example.smoiapp001.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.example.smoiapp001.ManageTransactionViewModel;
import com.example.smoiapp001.ManageTransactionViewModelFactory;
import com.example.smoiapp001.AppExecutors;
import com.example.smoiapp001.models.TransactionEntry;
import com.example.smoiapp001.R;
import com.example.smoiapp001.database.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ManageTransactionActivity extends AppCompatActivity {

    // Constants for transaction type
    private static final String EXPENSE_TRANSACTION_TYPE = "expense";
    private static final String INCOME_TRANSACTION_TYPE = "income";

    // Constant for date format
    private static final String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss";
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TRANSACTION_ID = "extraTransactionId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TRANSACTION_ID = "instanceTransactionId";

    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TRANSACTION_ID = -1;
    // Constant for logging
    private static final String TAG = ManageTransactionActivity.class.getSimpleName();
    // Fields for views
    EditText mEditTextDescription;
    EditText mEditTextCost;
    RadioButton mRadioButtonType;
    TextView mTextViewDate;
    Button mButton;

    private int mTaskId = DEFAULT_TRANSACTION_ID;

    // Member variable for the Database
    private AppDatabase mDb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_transaction);
        Log.i("Debug", "In ManageTransactionActivity");
        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());

        /*if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TRANSACTION_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TRANSACTION_ID, DEFAULT_TRANSACTION_ID);
        }*/

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TRANSACTION_ID)) {
            this.setTitle(R.string.update_transaction_activity_name);
            mButton.setText(R.string.update_button);
            if (mTaskId == DEFAULT_TRANSACTION_ID) {
                // populate the UI
                mTaskId = intent.getIntExtra(EXTRA_TRANSACTION_ID, DEFAULT_TRANSACTION_ID);

                ManageTransactionViewModelFactory factory = new ManageTransactionViewModelFactory(mDb, mTaskId);
                final ManageTransactionViewModel viewModel
                        = ViewModelProviders.of(this, factory).get(ManageTransactionViewModel.class);

                viewModel.getTransaction().observe(this, new Observer<TransactionEntry>() {
                    @Override
                    public void onChanged(@Nullable TransactionEntry transactionEntry) {
                        viewModel.getTransaction().removeObserver(this);
                        populateUI(transactionEntry);
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TRANSACTION_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mEditTextDescription = findViewById(R.id.et_transaction_description);
        mEditTextCost = findViewById(R.id.et_transaction_cost);
        mTextViewDate = findViewById(R.id.tv_transaction_date);

        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param transaction the taskEntry to populate the UI
     */
    private void  populateUI(TransactionEntry transaction) {
        if (transaction == null) {
            return;
        }

        mEditTextDescription.setText(transaction.getDescription());
        mEditTextCost.setText(Float.toString(Math.abs(transaction.getCost())));
        mTextViewDate.setText(dateFormat.format(transaction.getDate()));

        String type = EXPENSE_TRANSACTION_TYPE;
        if (transaction.getCost() >= 0) {
            type = INCOME_TRANSACTION_TYPE;
        }
        setTransactionTypeInViews(type);
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        String description = mEditTextDescription.getText().toString();
        float cost = Float.parseFloat(mEditTextCost.getText().toString());
        Date date = new Date();
        String type = getTransactionTypeFromViews();

        if (type.equals(EXPENSE_TRANSACTION_TYPE)) {
            cost *= -1;
        } else {
            /* Do nothing */
        }

        final TransactionEntry transaction = new TransactionEntry(description, cost, date);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTaskId == DEFAULT_TRANSACTION_ID) {
                    // insert new transaction
                    mDb.transactionDao().insertTransaction(transaction);
                } else {
                    //update transaction
                    transaction.setId(mTaskId);
                    mDb.transactionDao().updateTransaction(transaction);
                }
                finish();
            }
        });
    }

    public String getTransactionTypeFromViews() {
        String type;
        int checkedId =((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                type = EXPENSE_TRANSACTION_TYPE;
                break;
            case R.id.radButton2:
                type = INCOME_TRANSACTION_TYPE;
                break;
            default:
                type = EXPENSE_TRANSACTION_TYPE;
        }
        return type;
    }

    public void setTransactionTypeInViews(String type) {
        switch (type) {
            case EXPENSE_TRANSACTION_TYPE:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case INCOME_TRANSACTION_TYPE:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            default:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
        }
    }
}
