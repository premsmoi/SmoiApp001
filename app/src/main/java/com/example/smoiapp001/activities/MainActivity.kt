package com.example.smoiapp001.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager.widget.ViewPager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import com.example.smoiapp001.AlarmNotificationReceiver
import com.example.smoiapp001.adapters.PagerAdapter
import com.example.smoiapp001.R
import com.example.smoiapp001.utilities.FirebaseUtils
import com.example.smoiapp001.utilities.NotificationUtils
import com.example.smoiapp001.viewmodels.MainViewModel
import com.google.android.material.tabs.TabLayout


import java.sql.Time
import java.util.Calendar

import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pagerAdapter = PagerAdapter(supportFragmentManager)
        pager = findViewById(R.id.pager)
        pager.adapter = pagerAdapter
        pager.currentItem = 0

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE)
        tabLayout.setupWithViewPager(pager)

        NotificationUtils.loadNotification(this)

        Timber.i("Welcome to MainActivity")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_sync_firebase) {
            Timber.i("Synchronizing Transactions to Firebase..")
            FirebaseUtils.syncToFirebaseDatabase(viewModel, applicationContext)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
