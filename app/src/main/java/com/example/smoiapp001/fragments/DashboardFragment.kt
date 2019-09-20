package com.example.smoiapp001.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smoiapp001.R
import com.example.smoiapp001.activities.MainActivity
import com.example.smoiapp001.adapters.RankingTransactionAdapter
import com.example.smoiapp001.database.AppDatabase
import com.example.smoiapp001.utilities.DateUtils
import com.example.smoiapp001.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_dashbaord.*
import java.util.*

class DashboardFragment() : Fragment(), PagerFragment {

    private val name = "Dashboard"

    private lateinit var fragmentView: View
    private lateinit var mAllTimeAdapter: RankingTransactionAdapter
    private lateinit var mMonthlyAdapter: RankingTransactionAdapter
    private lateinit var mWeeklyAdapter: RankingTransactionAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_dashbaord, container, false)

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allTimeRecyclerView.layoutManager = LinearLayoutManager(context)
        allTimeRecyclerView.setHasFixedSize(true)

        monthlyRecyclerView.layoutManager = LinearLayoutManager(context)
        monthlyRecyclerView.setHasFixedSize(true)

        weeklyRecyclerView.layoutManager = LinearLayoutManager(context)
        weeklyRecyclerView.setHasFixedSize(true)

        initFilterSpinner()

        mAllTimeAdapter = RankingTransactionAdapter(context)
        mMonthlyAdapter = RankingTransactionAdapter(context)
        mWeeklyAdapter = RankingTransactionAdapter(context)

        allTimeRecyclerView.adapter = mAllTimeAdapter
        monthlyRecyclerView.adapter = mMonthlyAdapter
        weeklyRecyclerView.adapter = mWeeklyAdapter

        viewModel = (activity as MainActivity).mainViewModel
        setupViewModel()
        loadAllMostRecordedItems()
    }

    override fun getName() = name

    private fun initFilterSpinner() {
        val dataAdapter = ArrayAdapter.createFromResource(activity!!.applicationContext,
                R.array.filter_array, android.R.layout.simple_spinner_item)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = dataAdapter
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                if (position == SPINNER_COUNT_POSITION) {
                    loadAllMostRecordedItems()
                } else if (position == SPINNER_COST_POSITION) {
                    loadAllMostCostItems()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun setupViewModel() {
        viewModel.transactions.observe(this, Observer {
            if (filterSpinner.selectedItemPosition == SPINNER_COUNT_POSITION) {
                loadAllMostRecordedItems()
            } else if (filterSpinner.selectedItemPosition == SPINNER_COST_POSITION) {
                loadAllMostCostItems()
            }
        })
    }

    private fun loadMostCostItems(sinceDate: Long, adapter: RankingTransactionAdapter) {
        val items = AppDatabase.loadMostCostItems(TOP_NUMBER, sinceDate)
        adapter.setItems(items)
    }


    private fun loadMostRecordedItems(sinceDate: Long, adapter: RankingTransactionAdapter) {
        val items = AppDatabase.loadMostRecordedItems(TOP_NUMBER, sinceDate)
        adapter.setItems(items)
    }

    private fun loadAllMostCostItems() {
        val todayTimestamp = DateUtils.toTimestamp(Date())!!
        val last7daysTimestamp = todayTimestamp - DateUtils.WEEK_IN_MILLISECOND
        val last30daysTimestamp = todayTimestamp - DateUtils.MONTH_IN_MILLISECOND

        loadMostCostItems(0, mAllTimeAdapter)
        loadMostCostItems(last7daysTimestamp, mWeeklyAdapter)
        loadMostCostItems(last30daysTimestamp, mMonthlyAdapter)

    }

    private fun loadAllMostRecordedItems() {
        val todayTimestamp = DateUtils.toTimestamp(Date())!!
        val last7daysTimestamp = todayTimestamp - DateUtils.WEEK_IN_MILLISECOND
        val last30daysTimestamp = todayTimestamp - DateUtils.MONTH_IN_MILLISECOND

        loadMostRecordedItems(0, mAllTimeAdapter)
        loadMostRecordedItems(last7daysTimestamp, mWeeklyAdapter)
        loadMostRecordedItems(last30daysTimestamp, mMonthlyAdapter)

    }

    companion object {
        const val TOP_NUMBER = 5
        const val SPINNER_COUNT_POSITION = 0
        const val SPINNER_COST_POSITION = 1
    }

}
