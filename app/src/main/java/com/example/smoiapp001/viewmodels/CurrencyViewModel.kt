package com.example.smoiapp001.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.smoiapp001.models.Currency

class CurrencyViewModel(application: Application) : AndroidViewModel(application) {

    var currencies: MutableLiveData<ArrayList<Currency>>

    init {
        currencies = MutableLiveData()
    }

}