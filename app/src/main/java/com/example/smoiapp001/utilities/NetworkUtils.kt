package com.example.smoiapp001.utilities

import retrofit2.Retrofit
import com.example.smoiapp001.models.Currency
import com.example.smoiapp001.services.CurrencyService
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber


object NetworkUtils {

    fun getCurrencies(): ArrayList<Currency> {
        var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://api.exchangeratesapi.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create(CurrencyService::class.java)

        val call = service.getCurrency()

        val currency = call.execute().body()

        Timber.i("This base is %s",currency!!.base)
        Timber.i("This date is %s",currency!!.date)
        Timber.i("This rates is %s",currency.rates!!.toString())

        //var currencyListLiveData: MutableLiveData<ArrayList<Currency>> = MutableLiveData<ArrayList<Currency>>()
        var currencyList = ArrayList<Currency>()
        currencyList.add(Currency("EUR", currency.rates.eur))
        currencyList.add(Currency("USD", currency.rates.usd))
        currencyList.add(Currency("GBP", currency.rates.gbp))
        currencyList.add(Currency("JPY", currency.rates.jpy))
        currencyList.add(Currency("CNY", currency.rates.cny))
        currencyList.add(Currency("INR", currency.rates.inr))
        currencyList.add(Currency("KRW", currency.rates.krw))
        //currencyListLiveData.postValue(currencyList)

        return currencyList
    }
}