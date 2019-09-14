package com.example.smoiapp001.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

import android.graphics.Color
import androidx.core.content.ContextCompat
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import com.example.smoiapp001.adapters.PagerAdapter
import com.example.smoiapp001.R
import com.example.smoiapp001.utilities.FirebaseUtils
import com.example.smoiapp001.utilities.NotificationUtils
import com.example.smoiapp001.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


import timber.log.Timber


class MainActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pagerAdapter = PagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter
        pager.currentItem = 0

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        pager.tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        pager.tabLayout.setTabTextColors(Color.BLACK, Color.WHITE)
        pager.tabLayout.setupWithViewPager(pager)

        NotificationUtils.loadNotification(this)

        Timber.i("Created MainActivity")
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
            FirebaseUtils.syncToFirebaseDatabase(mainViewModel, applicationContext)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
