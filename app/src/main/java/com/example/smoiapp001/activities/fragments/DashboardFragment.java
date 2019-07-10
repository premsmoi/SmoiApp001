package com.example.smoiapp001.activities.fragments;

import android.arch.lifecycle.Observer;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.smoiapp001.R;
import com.example.smoiapp001.activities.MainActivity;
import com.example.smoiapp001.adapters.RankingTransactionAdapter;
import com.example.smoiapp001.database.AppDatabase;
import com.example.smoiapp001.database.models.TransactionEntry;
import com.example.smoiapp001.utilities.DateConverter;
import com.example.smoiapp001.viewmodels.MainViewModel;

import java.util.Date;
import java.util.List;

public class DashboardFragment extends Fragment {

    public static final String NAME = "Dashboard";

    private View fragmentView;
    private Spinner filterSpinner;
    private RecyclerView mAllTimeRecyclerView;
    private RecyclerView mMonthlyRecyclerView;
    private RecyclerView mWeeklyRecyclerView;
    private RankingTransactionAdapter mAllTimeAdapter;
    private RankingTransactionAdapter mMonthlyAdapter;
    private RankingTransactionAdapter mWeeklyAdapter;
    private MainViewModel viewModel;

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final Integer TOP_NUMBER = 5;
    public static final Integer SPINNER_COUNT_POSITION = 0;
    public static final Integer SPINNER_COST_POSITION = 1;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_dashbaord, container, false);
        mAllTimeRecyclerView = fragmentView.findViewById(R.id.recycler_view_all_time_transaction);
        mAllTimeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAllTimeRecyclerView.setHasFixedSize(true);

        mMonthlyRecyclerView = fragmentView.findViewById(R.id.recycler_view_monthly_transaction);
        mMonthlyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMonthlyRecyclerView.setHasFixedSize(true);

        mWeeklyRecyclerView = fragmentView.findViewById(R.id.recycler_view_weekly_transaction);
        mWeeklyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mWeeklyRecyclerView.setHasFixedSize(true);

        filterSpinner = fragmentView.findViewById(R.id.filter_spinner);
        initFilterSpinner();

        mAllTimeAdapter = new RankingTransactionAdapter(getContext());
        mMonthlyAdapter = new RankingTransactionAdapter(getContext());
        mWeeklyAdapter = new RankingTransactionAdapter(getContext());

        mAllTimeRecyclerView.setAdapter(mAllTimeAdapter);
        mMonthlyRecyclerView.setAdapter(mMonthlyAdapter);
        mWeeklyRecyclerView.setAdapter(mWeeklyAdapter);

        viewModel = ((MainActivity)getActivity()).getViewModel();
        setupViewModel();
        loadAllMostRecordedItems();

        return fragmentView;
    }

    private void initFilterSpinner() {
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.filter_array, android.R.layout.simple_spinner_item);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(dataAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == SPINNER_COUNT_POSITION) {
                    Log.i("Spinner", "Count Selected");
                    loadAllMostRecordedItems();
                }
                else if (position == SPINNER_COST_POSITION) {
                    Log.i("Spinner", "Cost Selected");
                    loadAllMostCostItems();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setupViewModel() {
        viewModel.getTransactions().observe(this, new Observer<List<TransactionEntry>>() {
            @Override
            public void onChanged(@Nullable List<TransactionEntry> transactionEntries) {
                if (filterSpinner.getSelectedItemPosition() == SPINNER_COUNT_POSITION) {
                    loadAllMostRecordedItems();
                } else if (filterSpinner.getSelectedItemPosition() == SPINNER_COST_POSITION) {
                    loadAllMostCostItems();
                }

            }
        });
    }
    private void loadMostCostItems(long sinceDate, RankingTransactionAdapter adapter) {
        Cursor items = AppDatabase.loadMostCostItems(TOP_NUMBER, sinceDate);
        adapter.setItems(items);
    }


    private void loadMostRecordedItems(long sinceDate, RankingTransactionAdapter adapter) {
        Cursor items = AppDatabase.loadMostRecordedItems(TOP_NUMBER, sinceDate);
        adapter.setItems(items);
    }

    private void loadAllMostCostItems() {
        long todayTimestamp = DateConverter.toTimestamp(new Date());
        long last7daysTimestamp = todayTimestamp - DateConverter.WEEK_IN_MILLISECOND;
        long last30daysTimestamp = todayTimestamp - DateConverter.MONTH_IN_MILLISECOND;

        loadMostCostItems(0, mAllTimeAdapter);
        loadMostCostItems(last7daysTimestamp, mWeeklyAdapter);
        loadMostCostItems(last30daysTimestamp, mMonthlyAdapter);

    }

    private void loadAllMostRecordedItems() {
        long todayTimestamp = DateConverter.toTimestamp(new Date());
        long last7daysTimestamp = todayTimestamp - DateConverter.WEEK_IN_MILLISECOND;
        long last30daysTimestamp = todayTimestamp - DateConverter.MONTH_IN_MILLISECOND;

        loadMostRecordedItems(0, mAllTimeAdapter);
        loadMostRecordedItems(last7daysTimestamp, mWeeklyAdapter);
        loadMostRecordedItems(last30daysTimestamp, mMonthlyAdapter);

    }

}
