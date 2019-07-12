package com.example.smoiapp001.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.example.smoiapp001.adapters.PagerAdapter;
import com.example.smoiapp001.R;
import com.example.smoiapp001.database.models.TransactionEntry;
import com.example.smoiapp001.viewmodels.MainViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ViewPager pager;
    private MainViewModel viewModel;

    private static final String TAG = MainActivity.class.getName();

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

        /*AssetDatabaseOpenHelper assetDatabaseOpenHelper = new AssetDatabaseOpenHelper(this);
        assetDatabaseOpenHelper.openDatabase();*/

    }

    public MainViewModel getViewModel() {
        return viewModel;
    }

}
