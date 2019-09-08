package com.example.smoiapp001.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.smoiapp001.AlarmNotificationReceiver;
import com.example.smoiapp001.adapters.PagerAdapter;
import com.example.smoiapp001.R;
import com.example.smoiapp001.database.models.TransactionEntry;
import com.example.smoiapp001.utilities.TransactionUtils;
import com.example.smoiapp001.viewmodels.MainViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    ViewPager pager;
    private MainViewModel viewModel;
    private DatabaseReference firebaseDB;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.pager) ;
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(0);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
        tabLayout.setupWithViewPager(pager);

        startAlarm();

    }

    private void startAlarm() {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        // SET TIME HERE
        Calendar firstCalendar= Calendar.getInstance();
        firstCalendar.set(Calendar.HOUR_OF_DAY,15);
        firstCalendar.set(Calendar.MINUTE,0);
        firstCalendar.set(Calendar.SECOND,0);

        Calendar secondCalendar= Calendar.getInstance();
        secondCalendar.set(Calendar.HOUR_OF_DAY,21);
        secondCalendar.set(Calendar.MINUTE,0);
        secondCalendar.set(Calendar.SECOND,0);

        long when1 = firstCalendar.getTimeInMillis();
        long when2 = secondCalendar.getTimeInMillis();
        //Intent myIntent = new Intent(this, AlarmNotificationReceiver.class);
        Intent myIntent = new Intent("com.example.smoiapp001.NOTIFICATION_ACTION");
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this,11,myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this,12,myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, when1, AlarmManager.INTERVAL_DAY,pendingIntent1);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, when2, AlarmManager.INTERVAL_DAY,pendingIntent2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sync_firebase) {
            Log.i(TAG, "Synchronizing Transactions to Firebase..");
            syncFirebaseDatabase();
            /*Calendar c = Calendar.getInstance();
            Log.i(TAG, ""+c.getTime());*/
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public MainViewModel getViewModel() {
        return viewModel;
    }

    private void syncFirebaseDatabase() {
        firebaseDB = FirebaseDatabase.getInstance().getReference();
        List<TransactionEntry> transactionEntries =  viewModel.getTransactions().getValue();
        for (final TransactionEntry transaction : transactionEntries) {
            final Map<String, Object> transactionObj = transaction.toMap();
            final DatabaseReference transactionsRef = firebaseDB.child("transactions");

            if (!TransactionUtils.isLocked(transaction)) {
                //Log.i(TAG, "It's not locked, what about next one?");
                continue;
            } else {
                //Log.i(TAG, "It's locked!");
            }

            transactionsRef.orderByChild("id").equalTo(transaction.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //Log.i(TAG, "exists!!! No need to add");
                    } else {
                        transactionsRef.push().setValue(transactionObj);
                        //Log.i(TAG, "not exist and locked, let's add new one");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        /*Log.i(TAG, "Transactions have been synchronized");
        Toast.makeText(this, "Transactions have been synchronized", Toast.LENGTH_LONG).show();*/
    }

}
