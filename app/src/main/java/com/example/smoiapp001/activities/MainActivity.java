package com.example.smoiapp001.activities;

import android.arch.lifecycle.LiveData;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.smoiapp001.PagerAdapter;
import com.example.smoiapp001.R;

public class MainActivity extends AppCompatActivity {
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.pager) ;
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(0);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
        tabLayout.setupWithViewPager(pager);

    }
}
