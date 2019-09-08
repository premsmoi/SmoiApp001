package com.example.smoiapp001.activities.fragments

import android.app.Activity

import androidx.lifecycle.Observer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView

import com.example.smoiapp001.utilities.DateUtils
import com.example.smoiapp001.viewmodels.MainViewModel
import com.example.smoiapp001.R
import com.example.smoiapp001.adapters.TransactionAdapter
import com.example.smoiapp001.activities.MainActivity
import com.example.smoiapp001.database.models.TransactionEntry
import com.example.smoiapp001.utilities.TransactionUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton

import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL

class MainFragment : Fragment(), TransactionAdapter.ItemClickListener {

    private lateinit var fragmentView: View
    private lateinit var dayCostTextView: TextView
    private lateinit var monthCostTextView: TextView
    private lateinit var dayAverageCostTextView: TextView
    private lateinit var selectDateButton: Button
    private lateinit var showAllCheckBox: CheckBox
    private lateinit var fabButton: FloatingActionButton

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: TransactionAdapter
    private lateinit var descriptionList: ArrayList<String>
    private lateinit var viewModel: MainViewModel
    private lateinit var selectedCalendar: Calendar

    private val requestCode = 11 // Used to identify the result

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_main, container, false)

        initViews()

        if (savedInstanceState != null && savedInstanceState.containsKey("selectedDate")) {
            selectDateButton.text = savedInstanceState.getString("selectedDate")
        }

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.setHasFixedSize(true)

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = TransactionAdapter(context!!, this)
        mRecyclerView.adapter = mAdapter

        /*viewModel = ViewModelProviders.of(this).get(MainViewModel.class);*/
        viewModel = (activity as MainActivity).viewModel

        val decoration = DividerItemDecoration(context!!.applicationContext, VERTICAL)
        mRecyclerView.addItemDecoration(decoration)

        descriptionList = ArrayList()
        setupViewModel()

        return fragmentView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("selectedDate", selectDateButton.text.toString())
    }

    private fun initViews() {
        /* Set the RecyclerView to its corresponding view */
        mRecyclerView = fragmentView.findViewById(R.id.recycler_view_transaction)
        dayCostTextView = fragmentView.findViewById(R.id.tv_daily_cost)
        monthCostTextView = fragmentView.findViewById(R.id.tv_monthly_cost)
        dayAverageCostTextView = fragmentView.findViewById(R.id.tv_day_avg_cost)
        showAllCheckBox = fragmentView.findViewById(R.id.checkbox_show_all)
        showAllCheckBox.setOnCheckedChangeListener { buttonView, isChecked -> setupViewModel() }
        selectDateButton = fragmentView.findViewById(R.id.bt_selected_date)
        selectDateButton.text = DateUtils.getNormalDateFormat().format(Date())
        selectDateButton.setOnClickListener { showDatePickerDialog() }
        fabButton = fragmentView.findViewById(R.id.fab)

        fabButton.setOnClickListener {
            findNavController(this@MainFragment)
                    .navigate(R.id.action_mainFragment_to_manageTransactionActivity)
        }
    }

    private fun setupViewModel() {
        viewModel.transactions.observe(this, Observer { transactionEntries ->
            val selectedEntries: ArrayList<TransactionEntry>
            val selectedCalendar = getSelectedCalendar()
            descriptionList = TransactionUtils.getDescriptionList(transactionEntries!!)

            if (!showAllCheckBox.isChecked) {
                selectedEntries = TransactionUtils.getTransactionByDate(transactionEntries, selectedCalendar)
                mAdapter.transactions = selectedEntries
            } else {
                mAdapter.transactions = transactionEntries
            }
            setDayCostView(TransactionUtils.calculateDayCost(transactionEntries, selectedCalendar))
            setMonthCostView(TransactionUtils.calculateMonthCost(transactionEntries, selectedCalendar))
            setDayAverageCostView(TransactionUtils.calculateMonthCost(transactionEntries, selectedCalendar))
        })
    }

    private fun setDayCostView(dayCost: Float) {
        var dayCostString = String.format(Locale.US, "%.2f", dayCost)

        if (dayCost < 0) {
            dayCostTextView.setTextColor(ContextCompat.getColor(context!!, R.color.colorRed))
        } else {
            dayCostTextView.setTextColor(ContextCompat.getColor(context!!, R.color.colorGreen))
            dayCostString = "+$dayCostString"
        }
        dayCostTextView.text = dayCostString
    }

    private fun setMonthCostView(monthCost: Float) {
        var monthCostString = String.format(Locale.US, "%.2f", monthCost)

        if (monthCost < 0) {
            monthCostTextView.setTextColor(ContextCompat.getColor(context!!, R.color.colorRed))
        } else {
            monthCostTextView.setTextColor(ContextCompat.getColor(context!!, R.color.colorGreen))
            monthCostString = "+$monthCostString"
        }
        monthCostTextView.text = monthCostString
    }

    private fun setDayAverageCostView(monthCost: Float) {
        var monthCostString = ""
        val today = Date().date
        val todayMonth = Date().month
        val selectedDay = DateUtils.getDayByNormalFormat(selectDateButton.text.toString())
        val selectedMonth = DateUtils.getMonthByNormalFormat(selectDateButton.text.toString())
        val selectedYear = DateUtils.getYearByNormalFormat(selectDateButton.text.toString())

        if (todayMonth == selectedMonth) {
            monthCostString = String.format(Locale.US, "%.2f", monthCost / today)
        } else if (todayMonth > selectedMonth) {
            val calendar = GregorianCalendar(selectedYear, selectedMonth, selectedDay)
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            monthCostString = String.format(Locale.US, "%.2f", monthCost / daysInMonth)
        }

        if (monthCost < 0) {
            dayAverageCostTextView.setTextColor(ContextCompat.getColor(context!!, R.color.colorRed))
        } else {
            dayAverageCostTextView.setTextColor(ContextCompat.getColor(context!!, R.color.colorGreen))
            monthCostString = "+$monthCostString"
        }
        dayAverageCostTextView.text = monthCostString
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
            val result: IntArray = data!!.getIntArrayExtra("selectedDate")
            val selectedDateString = DateUtils.buildNormalDateString(result[0], result[1], result[2])

            selectDateButton.text = selectedDateString
            val selectedCalendar = getSelectedCalendar()
            setDayCostView(TransactionUtils.calculateDayCost(
                    viewModel.transactions.value!!,
                    selectedCalendar))
            setMonthCostView(TransactionUtils.calculateMonthCost(
                    viewModel.transactions.value!!,
                    selectedCalendar))
            setDayAverageCostView(TransactionUtils.calculateMonthCost(
                    viewModel.transactions.value!!,
                    selectedCalendar))
            setupViewModel()

        }
    }

    private fun getSelectedCalendar(): Calendar {
        val selectedDateString = selectDateButton.text.toString()
        selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(DateUtils.getYearByNormalFormat(selectedDateString),
                DateUtils.getMonthByNormalFormat(selectedDateString),
                DateUtils.getDayByNormalFormat(selectedDateString))
        return selectedCalendar
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment()
        val date = selectDateButton.text.toString()
        val dateBundle = Bundle()
        dateBundle.putInt("day", DateUtils.getDayByNormalFormat(date))
        dateBundle.putInt("month", DateUtils.getMonthByNormalFormat(date))
        dateBundle.putInt("year", DateUtils.getYearByNormalFormat(date))
        newFragment.arguments = dateBundle
        newFragment.setTargetFragment(this@MainFragment, requestCode)
        newFragment.show(fragmentManager!!, "datePicker")
    }

    override fun onItemClickListener(itemId: Int) {
        val action
                = MainFragmentDirections.actionMainFragmentToManageTransactionActivity()
        action.itemId = itemId
        findNavController(this).navigate(action)
    }

    companion object {
        const val NAME = "Main"
    }

}
