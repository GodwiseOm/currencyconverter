package com.example.cowrywisecalculator.data

import android.util.Log
import com.example.cowrywisecalculator.data.model.FlagResponse
import com.example.cowrywisecalculator.data.model.FlagResponseItem
import com.example.cowrywisecalculator.data.model.RatesResponse
import com.example.cowrywisecalculator.data.model.Symbols
import com.example.cowrywisecalculator.domain.DataError
import com.example.cowrywisecalculator.domain.Result
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import retrofit2.Response
import safeCall
import java.io.IOException

class RemoteDataSource(private val api: ConversionApi) {

    suspend fun getRates(
        baseCurrency: String,
        conversionCurrency: String
    ): Result<RatesResponse, DataError> {
        return safeCall {
            api.getRates(baseCurrency, conversionCurrency)

        }
    }

    suspend fun getAllRates(): Result<RatesResponse, DataError> {
        return safeCall { api.getAllRates() }

    }

    suspend fun getSymbols(): Result<Symbols, DataError> {
        Log.d("RemoteDataSource", "fetching symbols started")
        return safeCall {
            api.getSymbols()

        }
    }

    suspend fun getConversionCurrencyImage(currency: String): Result<List<FlagResponseItem>, DataError> {
        return safeCall {
            api.getConversionCurrencyImage(currency)


        }
    }
}