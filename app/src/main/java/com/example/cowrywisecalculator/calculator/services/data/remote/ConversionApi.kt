package com.example.cowrywisecalculator.calculator.services.data.remote

import com.example.cowrywisecalculator.calculator.services.data.model.FlagResponse
import com.example.cowrywisecalculator.calculator.services.data.model.RatesResponse
import com.example.cowrywisecalculator.calculator.services.data.model.Symbols
import com.example.cowrywisecalculator.calculator.services.data.model.SymbolsResponse

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ConversionApi {

    @GET("latest")
    suspend fun getAllRates(@Query("access_key") accessKey: String = "17d55189d6a780ac6e2ae02ef61cca98"): Response<RatesResponse>

    @GET("latest")
    suspend fun getRates(
        @Query("base") baseCurrency: String,
        @Query("symbols") conversionCurrency: String,
        @Query("access_key") accessKey: String = "17d55189d6a780ac6e2ae02ef61cca98"
    ): Response<RatesResponse>


    @GET("{apiKey}/pair/{from}/{to}")
    suspend fun getExchangeRate(
        @Path("apiKey") apiKey: String,
        @Path("from") baseCurrency: String,
        @Path("to") conversionCurrency: String
    ): Response<RatesResponse>

    @GET("service/data/EXR/D.{currency}.EUR.SP00.A")
    suspend fun getECBExchangeRate(
        @Path("currency") targetCurrency: String,
        @Query("format") format: String = "jsondata",
        @Query("lastNObservations") limit: Int = 1
    ): Response<RatesResponse>


    @GET("symbols")
    suspend fun getSymbols(@Query("access_key") accessKey: String = "17d55189d6a780ac6e2ae02ef61cca98"): Response<SymbolsResponse>


    @GET
    suspend fun getBaseCurrencyImage(): String

    @GET
    suspend fun getConversionCurrencyName(): String

    @GET("{currency}")
    suspend fun getConversionCurrencyImage(@Path("currency") currency: String): Response<List<FlagResponse>>

    suspend fun getConversionRate() {}

}