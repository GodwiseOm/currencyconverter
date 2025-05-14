package com.example.cowrywisecalculator.presentation.conversion_screen

import com.example.cowrywisecalculator.domain.RatesResponse

data class ConversionScreenState (
    val currencyList:Map<String,Double> = emptyMap(),
    val symbols:List<String> = emptyList(),
    val conversionButtonClicked:()->Unit = {},
    val baseCurrency:String = "USD",
    val baseAmount:String = "0.0",
    val conversionCurrency:String = "NGN",
    val conversionAmount:String = "0.0",
    val baseImage:String = "https://flagcdn.com/w320/us.png",
    val conversionImage:String = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b7/Flag_of_Europe.svg/320px-Flag_of_Europe.svg.png",
    val allCurrency: RatesResponse = RatesResponse(null,null,null,null,null),
    val baseListVisibility:Boolean = false,
    val conversionListVisibility:Boolean = false,
    val error: String? = null
)
