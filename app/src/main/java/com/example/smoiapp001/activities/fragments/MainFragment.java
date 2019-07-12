package com.example.smoiapp001.activities.fragments;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.smoiapp001.viewmodels.MainViewModel;
import com.example.smoiapp001.R;
import com.example.smoiapp001.adapters.TransactionAdapter;
import com.example.smoiapp001.activities.MainActivity;
import com.example.smoiapp001.activities.ManageTransactionActivity;
import com.example.smoiapp001.database.models.TransactionEntry;
import com.example.smoiapp001.utilities.DateConverter;
import com.example.smoiapp001.utilities.TransactionUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class MainFragment extends Fragment implements TransactionAdapter.ItemClickListener {

    public static final String NAME = "Main";

    private static final String TAG = MainActivity.class.getSimpleName();

    private DatabaseReference firebaseDB;

    private View fragmentView;
    private TextView dayCostTextView;
    private TextView monthCostTextView;
    private TextView dayAverageCostTextView;
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
        fragmentView =  inflater.inflate(R.layout.fragment_main, container, false);

        initViews();

        if (savedInstanceState != null && savedInstanceState.containsKey("selectedDate")) {
            selectDateButton.setText(savedInstanceState.getString("selectedDate"));
        }

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TransactionAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        /*viewModel = ViewModelProviders.of(this).get(MainViewModel.class);*/
        viewModel = ((MainActivity)getActivity()).getViewModel();

        DividerItemDecoration decoration = new DividerItemDecoration(getContext().getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        descriptionList = new ArrayList<>();
        setupViewModel();

        firebaseDB = FirebaseDatabase.getInstance().getReference();

        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("selectedDate", selectDateButton.getText().toString());
    }

    private void initViews() {
        /* Set the RecyclerView to its corresponding view */
        mRecyclerView = fragmentView.findViewById(R.id.recycler_view_transaction);
        dayCostTextView = fragmentView.findViewById(R.id.tv_daily_cost);
        monthCostTextView = fragmentView.findViewById(R.id.tv_monthly_cost);
        dayAverageCostTextView = fragmentView.findViewById(R.id.tv_day_avg_cost);
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

                //syncFirebaseDatabase();

                if (!showAllCheckBox.isChecked()) {
                    selectedEntries = TransactionUtils.getTransactionByDate(transactionEntries, selectedCalendar);
                    mAdapter.setTransactions(selectedEntries);
                } else {
                    mAdapter.setTransactions(transactionEntries);
                }
                setDayCostView(TransactionUtils.calculateDayCost(transactionEntries, selectedCalendar));
                setMonthCostView(TransactionUtils.calculateMonthCost(transactionEntries, selectedCalendar));
                setDayAverageCostView(TransactionUtils.calculateMonthCost(transactionEntries, selectedCalendar));
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

    private void setDayAverageCostView(Float monthCost) {
        String monthCostString = "";
        int today = new Date().getDate();
        int todayMonth = new Date().getMonth();
        int selectedDay = DateConverter.getDayByNormalFormat(selectDateButton.getText().toString());
        int selectedMonth = DateConverter.getMonthByNormalFormat(selectDateButton.getText().toString());
        int selectedYear = DateConverter.getYearByNormalFormat(selectDateButton.getText().toString());
        /*Log.i(TAG, "todayMonth: "+todayMonth);
        Log.i(TAG, "selectedMonth: "+selectedMonth);*/
        Log.i(TAG, "today: "+today);
        Log.i(TAG, "selectedDay: "+selectedDay);

        if (todayMonth == selectedMonth) {
            monthCostString = String.format(Locale.US,"%.2f", monthCost/today);
        }
        else if (todayMonth > selectedMonth) {
            Calendar calendar = new GregorianCalendar(selectedYear, selectedMonth ,selectedDay);
            int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            monthCostString = String.format(Locale.US,"%.2f", monthCost/daysInMonth);
        }

        if (monthCost < 0) {
            dayAverageCostTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        } else {
            dayAverageCostTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            monthCostString = "+" + monthCostString;
        }
        dayAverageCostTextView.setText(monthCostString);
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
            setDayAverageCostView(TransactionUtils.calculateMonthCost(
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
        newFragment.setTargetFragment(MainFragment.this, REQUEST_CODE);
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

    private void syncFirebaseDatabase() {
        List<TransactionEntry> transactionEntries =  viewModel.getTransactions().getValue();
        for (TransactionEntry transaction : transactionEntries) {
            final Map<String, Object> transactionObj = transaction.toMap();
            final DatabaseReference transactionsRef = firebaseDB.child("transactions");

            transactionsRef.orderByChild("id").equalTo(transaction.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.i(TAG, "exists!!! No need to add");
                        } else {
                            transactionsRef.push().setValue(transactionObj);
                            Log.i(TAG, "not exist, let's add new one");
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //transactionsRef.push().setValue(transactionObj);
            /*break;*/
            /*if (transactionsRef.)
            String id = Integer.toString(transaction.getId());
            transactionsRef.child(id).child("description").setValue(transaction.getDescription());
            transactionsRef.child(id).child("cost").setValue(transaction.getCost());
            transactionsRef.child(id).child("date").setValue(DateConverter.toTimestamp(transaction.getDate()));
            break;*/
        }
    }
}
