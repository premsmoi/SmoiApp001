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
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.example.smoiapp001.ManageTransactionViewModel;
import com.example.smoiapp001.ManageTransactionViewModelFactory;
import com.example.smoiapp001.AppExecutors;
import com.example.smoiapp001.models.TransactionEntry;
import com.example.smoiapp001.R;
import com.example.smoiapp001.database.AppDatabase;
import com.example.smoiapp001.utilities.DateConverter;

import java.util.ArrayList;
import java.util.Date;


public class ManageTransactionActivity extends AppCompatActivity {

    // Constants for transaction type
    private static final String EXPENSE_TRANSACTION_TYPE = "expense";
    private static final String INCOME_TRANSACTION_TYPE = "income";

    // Constant for transaction timeout, 1 day
    private static final long TRANSACTION_TIMEOUT_PERIOD = DateConverter.DAY_IN_MILLISECOND;


    // Extra for the transaction ID to be received in the intent
    public static final String EXTRA_TRANSACTION_ID = "extraTransactionId";
    // Extra for the description list to be received in the intent
    public static final String EXTRA_DESCRIPTION_LIST = "extraDescriptionList";
    // Extra for the transaction ID to be received after rotation
    public static final String INSTANCE_TRANSACTION_ID = "instanceTransactionId";

    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TRANSACTION_ID = -1;
    // Constant for logging
    private static final String TAG = ManageTransactionActivity.class.getSimpleName();
    // Fields for views
    /*EditText mEditTextDescription;*/
    AutoCompleteTextView mEditTextDescription;
    EditText mEditTextCost;
    RadioGroup mRadioGroupType;
    TextView mTextViewDate;
    Button mActionButton;
    Button mDeleteButton;

    private int mTransactionId = DEFAULT_TRANSACTION_ID;

    // Member variable for the Database
    private AppDatabase mDb;

    private ArrayList<String> descriptionList = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_transaction);
        Log.i("Debug", "In ManageTransactionActivity");
        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());

        /*if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TRANSACTION_ID)) {
            mTransactionId = savedInstanceState.getInt(INSTANCE_TRANSACTION_ID, DEFAULT_TRANSACTION_ID);
        }*/

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_TRANSACTION_ID)) {
                this.setTitle(R.string.update_transaction_activity_name);
                mActionButton.setText(R.string.update_button);
                if (mTransactionId == DEFAULT_TRANSACTION_ID) {
                    // populate the UI
                    mTransactionId = intent.getIntExtra(EXTRA_TRANSACTION_ID, DEFAULT_TRANSACTION_ID);

                    ManageTransactionViewModelFactory factory = new ManageTransactionViewModelFactory(mDb, mTransactionId);
                    final ManageTransactionViewModel viewModel
                            = ViewModelProviders.of(this, factory).get(ManageTransactionViewModel.class);

                    viewModel.getTransaction().observe(this, new Observer<TransactionEntry>() {
                        @Override
                        public void onChanged(@Nullable TransactionEntry transactionEntry) {
                            viewModel.getTransaction().removeObserver(this);
                            populateUI(transactionEntry);
                            if (DateConverter.toTimestamp(new Date())- DateConverter.toTimestamp(transactionEntry.getDate())
                                    >= TRANSACTION_TIMEOUT_PERIOD) {
                                String costString = mEditTextCost.getText().toString();
                                costString += " THB";
                                mEditTextCost.setText(costString);
                                lockTransaction();
                            }
                        }
                    });
                }
            }
            if (intent.hasExtra(EXTRA_DESCRIPTION_LIST)) {
                descriptionList = intent.getStringArrayListExtra(EXTRA_DESCRIPTION_LIST);
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, descriptionList);
        mEditTextDescription.setAdapter(arrayAdapter);

        Log.i("List", descriptionList.toString());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TRANSACTION_ID, mTransactionId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        /*mEditTextDescription = findViewById(R.id.et_transaction_description);*/
        mEditTextDescription = findViewById(R.id.actv_transaction_description);
        mEditTextCost = findViewById(R.id.et_transaction_cost);
        mTextViewDate = findViewById(R.id.tv_transaction_date);

        mRadioGroupType = findViewById(R.id.radioGroup);

        mActionButton = findViewById(R.id.saveButton);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
        mDeleteButton = findViewById(R.id.deleteButton);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteButtonClicked();
            }
        });
    }

    private void lockTransaction(){
        mEditTextDescription.setFocusable(false);
        mEditTextDescription.setBackgroundColor(Color.TRANSPARENT);
        mEditTextCost.setFocusable(false);
        mEditTextCost.setBackgroundColor(Color.TRANSPARENT);
        mActionButton.setVisibility(View.INVISIBLE);
        mDeleteButton.setVisibility(View.INVISIBLE);

        int checkedIndex = mRadioGroupType.indexOfChild(findViewById(
                mRadioGroupType.getCheckedRadioButtonId()));
        for (int i = 0; i < mRadioGroupType.getChildCount(); i++) {
            if (i != checkedIndex) {
                mRadioGroupType.getChildAt(i).setEnabled(false);
            }
        }
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
        mTextViewDate.setText(DateConverter.getObviousDateFormat().format(transaction.getDate()));

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
                if (mTransactionId == DEFAULT_TRANSACTION_ID) {
                    // insert new transaction
                    mDb.transactionDao().insertTransaction(transaction);
                } else {
                    //update transaction
                    transaction.setId(mTransactionId);
                    mDb.transactionDao().updateTransaction(transaction);
                }
                finish();
            }
        });
    }

    public void onDeleteButtonClicked() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTransactionId != DEFAULT_TRANSACTION_ID) {
                    // delete selected transaction
                    Log.i("onDeleteButtonClicked", "Delete");
                    mDb.transactionDao().deleteTransactionById(mTransactionId);
                }
                finish();
            }
        });
    }

    public String getTransactionTypeFromViews() {
        String type;
        int checkedId = mRadioGroupType.getCheckedRadioButtonId();
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
