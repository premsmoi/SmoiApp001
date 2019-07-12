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

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.example.smoiapp001.utilities.AppExecutors;
import com.example.smoiapp001.loaders.LoadPopularCost;
import com.example.smoiapp001.loaders.LoadTransactionById;
import com.example.smoiapp001.database.models.TransactionEntry;
import com.example.smoiapp001.R;
import com.example.smoiapp001.database.AppDatabase;
import com.example.smoiapp001.utilities.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ManageTransactionActivity extends AppCompatActivity {

    // Constants for mTransaction type
    private static final String EXPENSE_TRANSACTION_TYPE = "expense";
    private static final String INCOME_TRANSACTION_TYPE = "income";

    // Constant for mTransaction timeout, 1 day
    private static final long TRANSACTION_TIMEOUT_PERIOD = DateConverter.DAY_IN_MILLISECOND;

    private static final int TRANSACTION_LOADER_ID = 21;
    private static final int POPULAR_COST_LOADER_ID = 22;

    // Extra for the mTransaction ID to be received in the intent
    public static final String EXTRA_TRANSACTION_ID = "extraTransactionId";
    // Extra for the description list to be received in the intent
    public static final String EXTRA_DESCRIPTION_LIST = "extraDescriptionList";
    // Extra for the mTransaction ID to be received after rotation
    public static final String INSTANCE_TRANSACTION_ID = "instanceTransactionId";
    public static final String EXTRA_DESCRIPTION_KEYWORD = "description-keyword";

    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TRANSACTION_ID = -1;
    // Constant for logging
    private static final String TAG = ManageTransactionActivity.class.getSimpleName();
    // Fields for views
    private LinearLayout mLinearLayout;
    private AutoCompleteTextView mEditTextDescription;
    private EditText mEditTextCost;
    private RadioGroup mRadioGroupType;
    private TextView mTextViewDate;
    private Button mActionButton;
    private Button mDeleteButton;

    private int mTransactionId = DEFAULT_TRANSACTION_ID;

    private TransactionEntry mTransaction;

    // Member variable for the Database
    private AppDatabase mDb;

    private ArrayList<String> descriptionList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_transaction);
        Log.i(TAG, "In ManageTransactionActivity");
        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_TRANSACTION_ID)) {
                mLinearLayout.setVisibility(View.INVISIBLE);
                this.setTitle(R.string.update_transaction_activity_name);
                mActionButton.setText(R.string.update_button);
                loadTransactionById(intent);
            }
            if (intent.hasExtra(EXTRA_DESCRIPTION_LIST)) {
                descriptionList = intent.getStringArrayListExtra(EXTRA_DESCRIPTION_LIST);
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, descriptionList);
        mEditTextDescription.setAdapter(arrayAdapter);

        /*Log.i("List", descriptionList.toString());*/

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
        mLinearLayout = findViewById(R.id.manage_transaction_layout);
        mEditTextDescription = findViewById(R.id.actv_transaction_description);
        mEditTextCost = findViewById(R.id.et_transaction_cost);
        mTextViewDate = findViewById(R.id.tv_transaction_date);
        mRadioGroupType = findViewById(R.id.radioGroup);
        mActionButton = findViewById(R.id.saveButton);
        mDeleteButton = findViewById(R.id.deleteButton);

        mEditTextDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mEditTextCost.setText(null);
            }
        });

        mEditTextDescription.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Selected!");
                loadRecommendedCost();
            }
        });

        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteButtonClicked();
            }
        });
    }

    private void lockTransaction() {
        String costString = mEditTextCost.getText().toString();
        costString += " THB";
        mEditTextCost.setText(costString);
        mEditTextDescription.setFocusable(false);
        mEditTextDescription.setBackgroundColor(Color.TRANSPARENT);
        mEditTextCost.setFocusable(false);
        mEditTextCost.setBackgroundColor(Color.TRANSPARENT);
        mActionButton.setVisibility(View.INVISIBLE);
        mDeleteButton.setVisibility(View.INVISIBLE);

        int checkedIndex = mRadioGroupType.indexOfChild(
                mRadioGroupType.findViewById(
                mRadioGroupType.getCheckedRadioButtonId()));
        Log.i(TAG, "checkedIndex: "+checkedIndex);
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
    private void populateUI(TransactionEntry transaction) {
        if (transaction == null) {
            return;
        }
        mEditTextDescription.setText(transaction.getDescription());
        mEditTextCost.setText(String.format(Locale.US,"%.2f", Math.abs(transaction.getCost())));
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
    private void onSaveButtonClicked() {
        String description = mEditTextDescription.getText().toString();
        float cost = Float.parseFloat(mEditTextCost.getText().toString());
        Date date = new Date();
        String type = getTransactionTypeFromViews();

        if (type.equals(EXPENSE_TRANSACTION_TYPE)) {
            cost *= -1;
        }

        final TransactionEntry transaction = new TransactionEntry(description, cost, date);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTransactionId == DEFAULT_TRANSACTION_ID) {
                    // insert new mTransaction
                    mDb.transactionDao().insertTransaction(transaction);
                } else {
                    //update mTransaction
                    transaction.setId(mTransactionId);
                    mDb.transactionDao().updateTransaction(transaction);
                }
                finish();
            }
        });
    }

    private void onDeleteButtonClicked() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTransactionId != DEFAULT_TRANSACTION_ID) {
                    // delete selected mTransaction
                    Log.i("onDeleteButtonClicked", "Delete");
                    mDb.transactionDao().deleteTransactionById(mTransactionId);
                }
                finish();
            }
        });
    }

    private String getTransactionTypeFromViews() {
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

    private void setTransactionTypeInViews(String type) {
        switch (type) {
            case EXPENSE_TRANSACTION_TYPE:
                mRadioGroupType.check(R.id.radButton1);
                break;
            case INCOME_TRANSACTION_TYPE:
                mRadioGroupType.check(R.id.radButton2);
                break;
            default:
                mRadioGroupType.check(R.id.radButton1);
        }
    }

    private void loadTransactionById(Intent intent) {
        if (mTransactionId == DEFAULT_TRANSACTION_ID) {
            mTransactionId = intent.getIntExtra(EXTRA_TRANSACTION_ID, DEFAULT_TRANSACTION_ID);
            LoaderManager.getInstance(this).initLoader(TRANSACTION_LOADER_ID,
                    null, transactionLoaderListener);
        }
    }

    private void loadRecommendedCost() {
        String keyword = mEditTextDescription.getText().toString();
        keyword = "%" + keyword + "%";
        Log.i("keyword", keyword);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_DESCRIPTION_KEYWORD, keyword);
        LoaderManager.getInstance(this).restartLoader(POPULAR_COST_LOADER_ID,
                bundle, popularCostLoaderListener);
    }

    private LoaderManager.LoaderCallbacks<TransactionEntry> transactionLoaderListener
            = new LoaderManager.LoaderCallbacks<TransactionEntry>() {
        @NonNull
        @Override
        public Loader<TransactionEntry> onCreateLoader(int id, @Nullable Bundle bundle) {
            return new LoadTransactionById(ManageTransactionActivity.this, mDb, mTransactionId);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<TransactionEntry> loader, TransactionEntry transactionEntry) {
            mTransaction = transactionEntry;
            populateUI(mTransaction);
            if (DateConverter.toTimestamp(new Date())- DateConverter.toTimestamp(transactionEntry.getDate())
                    >= TRANSACTION_TIMEOUT_PERIOD) {
                lockTransaction();
            }
            mLinearLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<TransactionEntry> loader) {
            mTransaction = null;
        }
    };

    private LoaderManager.LoaderCallbacks<Float> popularCostLoaderListener
            = new LoaderManager.LoaderCallbacks<Float>() {
        @NonNull
        @Override
        public Loader<Float> onCreateLoader(int id, @Nullable Bundle bundle) {
            String keyword = "???";
            if (bundle != null && bundle.containsKey(EXTRA_DESCRIPTION_KEYWORD)) {
                keyword = bundle.getString(EXTRA_DESCRIPTION_KEYWORD);
            }
            return new LoadPopularCost(ManageTransactionActivity.this, mDb, keyword);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Float> loader, Float popularCost) {
            mEditTextCost.setText(String.format(Locale.US, "%.2f", Math.abs(popularCost)));
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Float> loader) {

        }
    };

}
