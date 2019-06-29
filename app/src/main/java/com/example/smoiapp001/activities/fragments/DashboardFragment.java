package com.example.smoiapp001.activities.fragments;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.smoiapp001.MainViewModel;
import com.example.smoiapp001.R;
import com.example.smoiapp001.TransactionAdapter;
import com.example.smoiapp001.activities.MainActivity;
import com.example.smoiapp001.activities.ManageTransactionActivity;
import com.example.smoiapp001.models.TransactionEntry;
import com.example.smoiapp001.utilities.DateConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class DashboardFragment extends Fragment implements TransactionAdapter.ItemClickListener {

    public static final String NAME = "Dashboard";

    private static final String TAG = MainActivity.class.getSimpleName();

    private View fragmentView;
    private TextView dailyCostTextView;
    private TextView monthlyCostTextView;
    private Button selectDateButton;
    private CheckBox showAllCheckBox;

    private RecyclerView mRecyclerView;
    private TransactionAdapter mAdapter;
    private ArrayList<String> descriptionList;

    public static final int REQUEST_CODE = 11; // Used to identify the result

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentView =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        initViews();

        if (savedInstanceState != null && savedInstanceState.containsKey("selectedDate")) {
            selectDateButton.setText(savedInstanceState.getString("selectedDate"));
        }

        initCosts();

        // Set the RecyclerView to its corresponding view
        mRecyclerView = fragmentView.findViewById(R.id.recyclerViewDailyTransactions);
        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TransactionAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getContext().getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        FloatingActionButton fabButton = fragmentView.findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an ManageTransactionActivity
                Intent addTransactionIntent = new Intent(getContext(), ManageTransactionActivity.class);
                addTransactionIntent.putExtra(ManageTransactionActivity.EXTRA_DESCRIPTION_LIST, descriptionList);
                startActivity(addTransactionIntent);
            }
        });

        descriptionList = new ArrayList<String>();
        setupViewModel();

        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("selectedDate", selectDateButton.getText().toString());
    }

    private void initViews() {
        dailyCostTextView = fragmentView.findViewById(R.id.tv_daily_cost);
        monthlyCostTextView = fragmentView.findViewById(R.id.tv_monthly_cost);
        showAllCheckBox = fragmentView.findViewById(R.id.checkbox_show_all);
        showAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setupViewModel();
            }
        });
        selectDateButton = fragmentView.findViewById(R.id.bt_selected_date);
        selectDateButton.setText(DateConverter.getNormalDateFormat().format(new Date()));
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void initCosts(){
        calculateCost();
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTransactions().observe(this, new Observer<List<TransactionEntry>>() {
            @Override
            public void onChanged(@Nullable List<TransactionEntry> transactionEntries) {
                ArrayList<TransactionEntry> selectedEntries = new ArrayList<TransactionEntry>();
                SimpleDateFormat sdf = DateConverter.getNormalDateFormat();
                String selectedDate = selectDateButton.getText().toString();
                for (TransactionEntry transactionEntry: transactionEntries) {
                    String newDescription = transactionEntry.getDescription();
                    if (!descriptionList.contains(newDescription)) {
                        descriptionList.add(newDescription);
                    }
                    String entryDate = sdf.format(transactionEntry.getDate());
                    if (entryDate.equals(selectedDate)) {
                        selectedEntries.add(transactionEntry);
                    }
                }
                Log.d(TAG, "Updating list of transactions from LiveData in ViewModel");
                if (!showAllCheckBox.isChecked()) {
                    mAdapter.setTransactions(selectedEntries);
                } else {
                    mAdapter.setTransactions(transactionEntries);
                }
            }
        });
    }

    private void setDailyCostView(Float dayCost) {
        String dayCostString = Float.toString(dayCost);

        if (dayCost < 0) {
            dailyCostTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        } else {
            dailyCostTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            dayCostString = "+" + dayCostString;
        }
        dailyCostTextView.setText(dayCostString);
    }

    private void setMonthlyCostView(Float monthCost) {
        String monthCostString = Float.toString(monthCost);

        if (monthCost < 0) {
            monthlyCostTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        } else {
            monthlyCostTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            monthCostString = "+" + monthCostString;
        }
        monthlyCostTextView.setText(monthCostString);
    }

    private void setSelectedDate(String date) {
        selectDateButton.setText(date);
    }

    private void calculateCost() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTransactions().observe(this, new Observer<List<TransactionEntry>>() {
            float dailySum = 0;
            float monthlySum = 0;
            SimpleDateFormat sdf = new SimpleDateFormat(DateConverter.DATE_FORMAT_NORMAL);
            /*String todayString = sdf.format(new Date());*/
            String todayString = selectDateButton.getText().toString();
            String monthString = todayString.substring(3, 10);
            @Override
            public void onChanged(@Nullable List<TransactionEntry> transactionEntries) {
                Log.i("Month", monthString);
                for (TransactionEntry transaction : transactionEntries) {
                    String transactionDateString = sdf.format(transaction.getDate());
                    String transactionMonthString = transactionDateString.substring(3, 10);
                    if (todayString.equals(transactionDateString)) {
                        dailySum += transaction.getCost();
                        //Log.i("Cost", ""+transaction.getCost());
                    }
                    if (monthString.equals(transactionMonthString)) {
                        monthlySum += transaction.getCost();
                        //Log.i("Cost", ""+transaction.getCost());
                    }
                }
                setDailyCostView(dailySum);
                setMonthlyCostView(monthlySum);
                dailySum = 0;
                monthlySum = 0;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
           String selectedDateString = data.getStringExtra("selectedDate");
           setSelectedDate(selectedDateString);
           calculateCost();
           setupViewModel();
        }
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        String date = selectDateButton.getText().toString();
        Bundle dateBundle = new Bundle();
        dateBundle.putInt("day", DateConverter.getDayByNormalFormat(date));
        dateBundle.putInt("month", DateConverter.getMonthByNormalFormat(date));
        dateBundle.putInt("year", DateConverter.getYearByNormalFormat(date));
        newFragment.setArguments(dateBundle);
        newFragment.setTargetFragment(DashboardFragment.this, REQUEST_CODE);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch ManageTransactionActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(getContext(), ManageTransactionActivity.class);
        intent.putExtra(ManageTransactionActivity.EXTRA_TRANSACTION_ID, itemId);
        intent.putExtra(ManageTransactionActivity.EXTRA_DESCRIPTION_LIST, descriptionList);
        startActivity(intent);
    }
}
