package com.example.smoiapp001.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smoiapp001.MyApplication
import com.example.smoiapp001.R
import com.example.smoiapp001.adapters.CurrencyAdapter
import com.example.smoiapp001.factories.CurrencyViewModelFactory
import com.example.smoiapp001.viewmodels.CurrencyViewModel
import kotlinx.android.synthetic.main.fragment_currency.*
import javax.inject.Inject

class CurrencyFragment() : Fragment(), PagerFragment {

    private val name = "Currencies"

    @Inject
    lateinit var currencyAdapter: CurrencyAdapter
    @Inject
    lateinit var currencyViewModelFactory: CurrencyViewModelFactory
    @Inject
    lateinit var decoration: DividerItemDecoration

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_currency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyApplication.mAppComponent.inject(this)
        currencyRecyclerView.layoutManager = LinearLayoutManager(context)
        currencyRecyclerView.setHasFixedSize(true)
        currencyRecyclerView.adapter = currencyAdapter
        currencyRecyclerView.addItemDecoration(decoration)
        val viewModel = ViewModelProviders
                .of(this, CurrencyViewModelFactory())
                .get(CurrencyViewModel::class.java)

        viewModel.currencies.observe(this, Observer {
            currencyAdapter.setCurrencies(it)
        })
    }

    override fun getName() = name
}
