package com.example.smoiapp001.activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.smoiapp001.R
import com.example.smoiapp001.adapters.CurrencyAdapter
import com.example.smoiapp001.utilities.AppExecutors
import com.example.smoiapp001.utilities.NetworkUtils
import com.example.smoiapp001.viewmodels.CurrencyViewModel
import kotlinx.android.synthetic.main.fragment_currency.*

class CurrencyFragment : Fragment() {

    private lateinit var fragmentView: View
    private lateinit var mCurrencyAdapter: CurrencyAdapter
    private lateinit var viewModel: CurrencyViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_currency, container, false)

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCurrencyAdapter = CurrencyAdapter(context)
        currencyRecyclerView.layoutManager = LinearLayoutManager(context)
        currencyRecyclerView.setHasFixedSize(true)
        currencyRecyclerView.adapter = mCurrencyAdapter

        val decoration = DividerItemDecoration(context!!.applicationContext, DividerItemDecoration.VERTICAL)
        currencyRecyclerView.addItemDecoration(decoration)
        viewModel = ViewModelProviders.of(this).get(CurrencyViewModel::class.java)

        viewModel.currencies.observe(this, Observer {
            mCurrencyAdapter.setCurrencies(it)
        })
        getCurrencies()
    }

    private fun getCurrencies() {
        AppExecutors.instance.networkIO().execute() {
            viewModel.currencies.postValue(NetworkUtils.getCurrencies())
        }
    }

    companion object {
        val NAME = "Currencies"
    }
}
