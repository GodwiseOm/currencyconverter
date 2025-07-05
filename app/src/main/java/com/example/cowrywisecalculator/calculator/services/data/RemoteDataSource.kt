package com.example.cowrywisecalculator.calculator.services.data

import android.util.Log
import com.example.cowrywisecalculator.calculator.services.data.model.FlagResponse
import com.example.cowrywisecalculator.calculator.services.data.model.FlagResponseItem
import com.example.cowrywisecalculator.calculator.services.data.model.RatesResponse
import com.example.cowrywisecalculator.calculator.services.data.model.Symbols
import com.example.cowrywisecalculator.calculator.domain.DataError
import com.example.cowrywisecalculator.calculator.domain.Result
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import retrofit2.Response
import safeCall
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class RemoteDataSource @Inject constructor(
    @Named("fixer") private val fixerApi: ConversionApi,
    @Named("exchange") private val exchangeRateApi: ConversionApi,
    @Named("countries") private val countriesApi: ConversionApi
) {

    suspend fun getRates(
        baseCurrency: String,
        conversionCurrency: String
    ): Result<RatesResponse, DataError> {
        return safeCall {
            // Try primary provider ( - more reliable but requires API key)
            val response = exchangeRateApi.getRates(baseCurrency, conversionCurrency)
            if (response.isSuccessful && response.body() != null) {
              response
            } else {
                fixerApi.getRates(baseCurrency, conversionCurrency)

            }

        }
    }

    suspend fun getAllRates(): Result<RatesResponse, DataError> {
        return safeCall {
            val  response = fixerApi.getAllRates()
        if (response.isSuccessful && response.body() != null) {
            response
        } else {
            exchangeRateApi.getAllRates()

        }        }

    }

    suspend fun getSymbols(): Result<com.example.cowrywisecalculator.calculator.services.data.model.Symbols, DataError> {
        Log.d("RemoteDataSource", "fetching symbols started")
        return safeCall {
          val response = fixerApi.getSymbols()
            if (response.isSuccessful && response.body() != null) {
                response
            }
            else{
                exchangeRateApi.getSymbols()

            }

        }
    }

    suspend fun getConversionCurrencyImage(currency: String): Result<List<com.example.cowrywisecalculator.calculator.services.data.model.FlagResponseItem>, DataError> {
        return safeCall {
            countriesApi.getConversionCurrencyImage(currency)


        }
    }
}