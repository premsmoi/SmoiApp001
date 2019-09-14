package com.example.smoiapp001.models

import com.google.gson.annotations.SerializedName

data class CurrenciesInfo (
        @SerializedName("rates") val rates: Rates,
        @SerializedName("base") val base: String,
        @SerializedName("date") val date: String
)

data class Rates (
        @SerializedName("EUR") val eur: Float,
        @SerializedName("USD") val usd: Float,
        @SerializedName("GBP") val gbp: Float,
        @SerializedName("JPY") val jpy: Float,
        @SerializedName("CNY") val cny: Float,
        @SerializedName("INR") val inr: Float,
        @SerializedName("KRW") val krw: Float
)