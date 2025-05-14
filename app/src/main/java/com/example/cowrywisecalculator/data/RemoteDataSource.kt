package com.example.cowrywisecalculator.data

import com.example.cowrywisecalculator.data.model.FlagResponse
import com.example.cowrywisecalculator.data.model.RatesResponse
import com.example.cowrywisecalculator.data.model.Symbols
import com.example.cowrywisecalculator.domain.DataError
import com.example.cowrywisecalculator.domain.Result
import retrofit2.HttpException
import java.io.IOException

class RemoteDataSource(private val api: ConversionApi) {
    
    suspend fun getRates(baseCurrency: String, conversionCurrency: String): Result<RatesResponse, DataError> {
        return try {
            val response = api.getRates(baseCurrency, conversionCurrency)
            if (response.success == true) {
                Result.Success(response)
            } else {
                Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: IOException) {
            Result.Error(DataError.Remote.NO_INTERNET)
        } catch (e: HttpException) {
            when (e.code()) {
                429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
                408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
                else -> Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun getAllRates(): Result<RatesResponse, DataError> {
        return try {
            val response = api.getAllRates()
            if (response.success == true) {
                Result.Success(response)
            } else {
                Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: IOException) {
            Result.Error(DataError.Remote.NO_INTERNET)
        } catch (e: HttpException) {
            when (e.code()) {
                429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
                408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
                else -> Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun getSymbols(): Result<Symbols, DataError> {
        return try {
            val response = api.getSymbols()
            if (response.success) {
                Result.Success(response)
            } else {
                Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: IOException) {
            Result.Error(DataError.Remote.NO_INTERNET)
        } catch (e: HttpException) {
            when (e.code()) {
                429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
                408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
                else -> Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun getConversionCurrencyImage(currency: String): Result<FlagResponse, DataError> {
        return try {
            val response = api.getConversionCurrencyImage(currency)
            Result.Success(response)
        } catch (e: IOException) {
            Result.Error(DataError.Remote.NO_INTERNET)
        } catch (e: HttpException) {
            when (e.code()) {
                429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
                408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
                else -> Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
} 