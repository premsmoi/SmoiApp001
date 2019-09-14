package com.example.smoiapp001.services

import com.example.smoiapp001.models.CurrenciesInfo
import retrofit2.Call
import retrofit2.http.GET


interface CurrencyService {

    @GET("/latest?base=THB&symbols=USD,EUR,GBP,JPY,CNY,INR,KRW")
    fun getCurrency(): Call<CurrenciesInfo>
}