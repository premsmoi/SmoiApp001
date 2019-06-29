package com.example.smoiapp001.activities.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smoiapp001.MainViewModel;
import com.example.smoiapp001.models.TransactionEntry;
import com.example.smoiapp001.R;
import com.example.smoiapp001.TransactionAdapter;
import com.example.smoiapp001.activities.MainActivity;
import com.example.smoiapp001.activities.ManageTransactionActivity;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class AllTransactionsFragment extends Fragment implements TransactionAdapter.ItemClickListener {

    public static final String NAME = "All";

    private View fragmentView;
    private ArrayList<String> descriptionList;

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private TransactionAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_all_transactions, container, false);
        // Set the RecyclerView to its corresponding view
        mRecyclerView = fragmentView.findViewById(R.id.recyclerViewAllTransactions);
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
        /*mDb = AppDatabase.getInstance(getContext().getApplicationContext());*/
        setupViewModel();

        return fragmentView;
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTransactions().observe(this, new Observer<List<TransactionEntry>>() {
            @Override
            public void onChanged(@Nullable List<TransactionEntry> transactionEntries) {
                for (TransactionEntry transactionEntry: transactionEntries) {
                    String newDescription = transactionEntry.getDescription();
                    if (!descriptionList.contains(newDescription)) {
                        descriptionList.add(newDescription);
                    }
                }
                Log.d(TAG, "Updating list of transactions from LiveData in ViewModel");
                mAdapter.setTransactions(transactionEntries);
            }
        });
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
