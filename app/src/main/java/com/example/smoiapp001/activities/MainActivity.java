package com.example.smoiapp001.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.smoiapp001.adapters.PagerAdapter;
import com.example.smoiapp001.R;
import com.example.smoiapp001.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity {
    ViewPager pager;
    private MainViewModel viewModel;

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

    }

    public MainViewModel getViewModel() {
        return viewModel;
    }
}
