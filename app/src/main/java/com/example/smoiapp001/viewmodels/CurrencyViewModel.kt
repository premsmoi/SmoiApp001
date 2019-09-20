package com.example.smoiapp001.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smoiapp001.models.Currency
import com.example.smoiapp001.utilities.AppExecutors
import com.example.smoiapp001.utilities.NetworkUtils

class CurrencyViewModel() : ViewModel() {

    var currencies: MutableLiveData<ArrayList<Currency>> = MutableLiveData()

    init {
        AppExecutors.instance.networkIO().execute() {
            currencies.postValue(NetworkUtils.getCurrencies())
        }
    }

}