package com.example.cowrywisecalculator.data

import com.example.cowrywisecalculator.data.model.FlagResponse
import com.example.cowrywisecalculator.data.model.RatesResponse
import com.example.cowrywisecalculator.data.model.Symbols
import com.example.cowrywisecalculator.domain.DataError
import com.example.cowrywisecalculator.domain.Result
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ConversionApi {

    @GET("latest")
    suspend fun getAllRates(@Query("access_key") accessKey: String = "17d55189d6a780ac6e2ae02ef61cca98"): Result<RatesResponse, DataError>

    @GET("latest")
    suspend fun getRates(
        @Query("base") baseCurrency: String, @Query("symbols") conversionCurrency: String
    ): Result<RatesResponse, DataError>

    @GET("symbols")
    suspend fun getSymbols(@Query("access_key") accessKey: String = "17d55189d6a780ac6e2ae02ef61cca98"): Result<Symbols, DataError>


    @GET
    suspend fun getBaseCurrencyImage(): Result<String,DataError>

    @GET
    suspend fun getConversionCurrencyName(): Result<String,DataError>

    @GET("{currency}")
    suspend fun getConversionCurrencyImage(@Path("currency") currency: String): Result<FlagResponse,DataError>

    suspend fun getConversionRate() {}

}