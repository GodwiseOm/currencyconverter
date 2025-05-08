package com.example.cowrywisecalculator.data

import com.example.cowrywisecalculator.domain.RatesResponse
import com.example.cowrywisecalculator.domain.model.Symbols
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ConversionApi {

    @GET("latest")
    suspend fun getAllRates(@Query("access_key") accessKey: String = "17d55189d6a780ac6e2ae02ef61cca98"): RatesResponse

    @GET("latest")
    suspend fun getRates(
        @Query("base") baseCurrency: String,
        @Query("symbols") conversionCurrency: String
    ): RatesResponse

    @GET("symbols")
    suspend fun getSymbols(@Query("access_key") accessKey: String = "17d55189d6a780ac6e2ae02ef61cca98"): Symbols


    @GET
    suspend fun getBaseCurrencyImage():String

    @GET
    suspend fun getConversionCurrencyName():String

    @GET
    suspend fun getConversionCurrencyImage(@Path("currency") currency:String):String

    suspend fun getConversionRate(){}

}