package com.example.smoiapp001.activities.fragments;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import com.example.smoiapp001.utilities.TransactionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class DashboardFragment extends Fragment implements TransactionAdapter.ItemClickListener {

    public static final String NAME = "Dashboard";

    private static final String TAG = MainActivity.class.getSimpleName();

    private View fragmentView;
    private TextView dayCostTextView;
    private TextView monthCostTextView;
    private Button selectDateButton;
    private CheckBox showAllCheckBox;
    private FloatingActionButton fabButton;

    private RecyclerView mRecyclerView;
    private TransactionAdapter mAdapter;
    private ArrayList<String> descriptionList;
    private MainViewModel viewModel;
    private Calendar selectedCalendar;

    public static final int REQUEST_CODE = 11; // Used to identify the result

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentView =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        initViews();

        if (savedInstanceState != null && savedInstanceState.containsKey("selectedDate")) {
            selectDateButton.setText(savedInstanceState.getString("selectedDate"));
        }

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TransactionAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        DividerItemDecoration decoration = new DividerItemDecoration(getContext().getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        descriptionList = new ArrayList<>();
        setupViewModel();

        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("selectedDate", selectDateButton.getText().toString());
    }

    private void initViews() {
        /* Set the RecyclerView to its corresponding view */
        mRecyclerView = fragmentView.findViewById(R.id.recyclerViewDailyTransactions);
        dayCostTextView = fragmentView.findViewById(R.id.tv_daily_cost);
        monthCostTextView = fragmentView.findViewById(R.id.tv_monthly_cost);
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
        fabButton = fragmentView.findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an ManageTransactionActivity
                Intent addTransactionIntent = new Intent(getContext(), ManageTransactionActivity.class);
                addTransactionIntent.putExtra(ManageTransactionActivity.EXTRA_DESCRIPTION_LIST, descriptionList);
                startActivity(addTransactionIntent);
            }
        });
    }

    private void setupViewModel() {
        viewModel.getTransactions().observe(this, new Observer<List<TransactionEntry>>() {
            @Override
            public void onChanged(@Nullable List<TransactionEntry> transactionEntries) {
                ArrayList<TransactionEntry> selectedEntries;
                Calendar selectedCalendar = getSelectedCalendar();
                descriptionList = TransactionUtils.getDescriptionList(transactionEntries);

                if (!showAllCheckBox.isChecked()) {
                    selectedEntries = TransactionUtils.getTransactionByDate(transactionEntries, selectedCalendar);
                    mAdapter.setTransactions(selectedEntries);
                } else {
                    mAdapter.setTransactions(transactionEntries);
                }
                setDayCostView(TransactionUtils.calculateDayCost(transactionEntries, selectedCalendar));
                setMonthCostView(TransactionUtils.calculateMonthCost(transactionEntries, selectedCalendar));
            }

        });
    }

    private void setDayCostView(Float dayCost) {
        String dayCostString = String.format(Locale.US,"%.2f", dayCost);

        if (dayCost < 0) {
            dayCostTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        } else {
            dayCostTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            dayCostString = "+" + dayCostString;
        }
        dayCostTextView.setText(dayCostString);
    }

    private void setMonthCostView(Float monthCost) {
        String monthCostString = String.format(Locale.US,"%.2f", monthCost);

        if (monthCost < 0) {
            monthCostTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        } else {
            monthCostTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            monthCostString = "+" + monthCostString;
        }
        monthCostTextView.setText(monthCostString);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            int[] result;
            result = data.getIntArrayExtra("selectedDate");
            String selectedDateString = DateConverter.
                    buildNormalDateString(result[0], result[1], result[2]);

            selectDateButton.setText(selectedDateString);
            Calendar selectedCalendar = getSelectedCalendar();
            setDayCostView(TransactionUtils.calculateDayCost(
                    viewModel.getTransactions().getValue(),
                    selectedCalendar));
            setMonthCostView(TransactionUtils.calculateMonthCost(
                   viewModel.getTransactions().getValue(),
                   selectedCalendar));
            setupViewModel();

            /*Log.i("selectedDate", selectedDateString);
            Log.i("Set Day Cost", ""+TransactionUtils.calculateDayCost(
                    viewModel.getTransactions().getValue(),
                    selectedCalendar));
            Log.i("Set Month Cost", ""+TransactionUtils.calculateMonthCost(
                    viewModel.getTransactions().getValue(),
                    selectedCalendar));*/
        }
    }

    public Calendar getSelectedCalendar() {
        String selectedDateString = selectDateButton.getText().toString();
        selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(DateConverter.getYearByNormalFormat(selectedDateString),
                DateConverter.getMonthByNormalFormat(selectedDateString),
                DateConverter.getDayByNormalFormat(selectedDateString));
        return selectedCalendar;
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
        Log.i("Intent", "Go to ManageTransactionActivity");
        startActivity(intent);
    }
}
