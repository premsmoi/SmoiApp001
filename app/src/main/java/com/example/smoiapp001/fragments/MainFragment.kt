package com.example.smoiapp001.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smoiapp001.MyApplication
import com.example.smoiapp001.R
import com.example.smoiapp001.activities.MainActivity
import com.example.smoiapp001.adapters.TransactionAdapter
import com.example.smoiapp001.database.models.TransactionEntry
import com.example.smoiapp001.utilities.DateUtils
import com.example.smoiapp001.utilities.TransactionUtils
import com.example.smoiapp001.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*
import javax.inject.Inject

class MainFragment() : Fragment(), PagerFragment, TransactionAdapter.ItemClickListener {

    private val name = "Main"

    private lateinit var descriptionList: ArrayList<String>
    private lateinit var viewModel: MainViewModel
    private lateinit var selectedCalendar: Calendar

    @Inject
    lateinit var mAdapter: TransactionAdapter

    private val requestCode = 11 // Used to identify the result

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyApplication.mAppComponent.inject(this)
        showAllCheckbox.setOnCheckedChangeListener { buttonView, isChecked -> setupViewModel() }
        dateSelectionButton.text = DateUtils.getNormalDateFormat().format(Date())
        dateSelectionButton.setOnClickListener { showDatePickerDialog() }
        fabButton.setOnClickListener {
            findNavController(this@MainFragment)
                    .navigate(R.id.action_mainFragment_to_manageTransactionActivity)
        }

        if (savedInstanceState != null && savedInstanceState.containsKey("selectedDate")) {
            dateSelectionButton.text = savedInstanceState.getString("selectedDate")
        }

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        transactionRecyclerView.layoutManager = LinearLayoutManager(context)
        transactionRecyclerView.setHasFixedSize(true)

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = TransactionAdapter(context!!, this)
        transactionRecyclerView.adapter = mAdapter

        viewModel = (activity as MainActivity).mainViewModel

        val decoration = DividerItemDecoration(context!!.applicationContext, VERTICAL)
        transactionRecyclerView.addItemDecoration(decoration)

        descriptionList = ArrayList()
        setupViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("selectedDate", dateSelectionButton.text.toString())
    }

    override fun getName() = name

    private fun setupViewModel() {
        viewModel.transactions.observe(this, Observer { transactionEntries ->
            val selectedEntries: List<TransactionEntry>
            val selectedCalendar = getSelectedCalendar()
            descriptionList = TransactionUtils.getDescriptionList(transactionEntries!!)

            if (!showAllCheckbox.isChecked) {
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
            dailyCost.setTextColor(ContextCompat.getColor(context!!, R.color.colorRed))
        } else {
            dailyCost.setTextColor(ContextCompat.getColor(context!!, R.color.colorGreen))
            dayCostString = "+$dayCostString"
        }
        dailyCost.text = dayCostString
    }

    private fun setMonthCostView(monthCost: Float) {
        var monthCostString = String.format(Locale.US, "%.2f", monthCost)

        if (monthCost < 0) {
            monthlyCost.setTextColor(ContextCompat.getColor(context!!, R.color.colorRed))
        } else {
            monthlyCost.setTextColor(ContextCompat.getColor(context!!, R.color.colorGreen))
            monthCostString = "+$monthCostString"
        }
        monthlyCost.text = monthCostString
    }

    private fun setDayAverageCostView(monthCost: Float) {
        var monthCostString = ""
        val today = Date().date
        val todayMonth = Date().month
        val selectedDay = DateUtils.getDayByNormalFormat(dateSelectionButton.text.toString())
        val selectedMonth = DateUtils.getMonthByNormalFormat(dateSelectionButton.text.toString())
        val selectedYear = DateUtils.getYearByNormalFormat(dateSelectionButton.text.toString())

        if (todayMonth == selectedMonth) {
            monthCostString = String.format(Locale.US, "%.2f", monthCost / today)
        } else if (todayMonth > selectedMonth) {
            val calendar = GregorianCalendar(selectedYear, selectedMonth, selectedDay)
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            monthCostString = String.format(Locale.US, "%.2f", monthCost / daysInMonth)
        }

        if (monthCost < 0) {
            averageCost.setTextColor(ContextCompat.getColor(context!!, R.color.colorRed))
        } else {
            averageCost.setTextColor(ContextCompat.getColor(context!!, R.color.colorGreen))
            monthCostString = "+$monthCostString"
        }
        averageCost.text = monthCostString
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
            val result: IntArray = data!!.getIntArrayExtra("selectedDate")
            val selectedDateString = DateUtils.buildNormalDateString(result[0], result[1], result[2])

            dateSelectionButton.text = selectedDateString
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
        val selectedDateString = dateSelectionButton.text.toString()
        selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(DateUtils.getYearByNormalFormat(selectedDateString),
                DateUtils.getMonthByNormalFormat(selectedDateString),
                DateUtils.getDayByNormalFormat(selectedDateString))
        return selectedCalendar
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment()
        val date = dateSelectionButton.text.toString()
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

}
