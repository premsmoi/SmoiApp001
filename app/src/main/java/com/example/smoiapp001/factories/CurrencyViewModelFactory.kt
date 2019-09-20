package com.example.smoiapp001.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smoiapp001.viewmodels.CurrencyViewModel

class CurrencyViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CurrencyViewModel() as T
    }
}