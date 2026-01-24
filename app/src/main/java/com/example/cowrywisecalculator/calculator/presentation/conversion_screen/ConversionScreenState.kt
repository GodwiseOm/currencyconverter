package com.example.cowrywisecalculator.calculator.presentation.conversion_screen

import com.example.cowrywisecalculator.calculator.services.data.model.RatesResponse
import kotlinx.coroutines.flow.StateFlow


data class ConversionScreenState (
    val currencyList:Map<String,Double> = emptyMap(),
    val showLoading:Boolean = false,

    val symbols:List<String> = emptyList(),
    val conversionButtonClicked:()->Unit = {},
    val baseCurrency:String = "USD",
    val baseAmount:String = "",
    val conversionCurrency:String = "EUR",
    val conversionAmount:String = "",
    val baseImage:String = "https://flagcdn.com/w320/us.png",
    val conversionImage:String = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b7/Flag_of_Europe.svg/320px-Flag_of_Europe.svg.png",
    val allCurrency: RatesResponse = RatesResponse(
        null,
        null,
        null,
        null,
        null,
        baseCode = null,
        conversionRate = null,
        conversionResult = null,
        documentation = null,
        result = null,
        targetCode = null,
        termsOfUse = null,
        timeLastUpdateUnix = null,
        timeLastUpdateUtc = null,
        timeNextUpdateUnix = null,
        timeNextUpdateUtc = null
    ),
    val baseListVisibility:Boolean = false,
    val conversionListVisibility:Boolean = false,
    val error: String? = null
)
