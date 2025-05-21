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
import java.io.IOException

class RemoteDataSource(private val api: ConversionApi) {
    
    suspend fun getRates(baseCurrency: String, conversionCurrency: String): Result<RatesResponse, DataError> {
        return try {
            val response = api.getRates(baseCurrency, conversionCurrency)
            if (response.success == true) {
                Log.d("RemoteDataSource", "successfully fetched rates: $response")
                Result.Success(response)

            } else {

                Log.d("RemoteDataSource", "failed to fetch rates: $response")
                Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: IOException) {
            Log.d("RemoteDataSource", "IOException: ${e.message}")
            Result.Error(DataError.Remote.NO_INTERNET)
        } catch (e: HttpException) {
            Log.d("RemoteDataSource", "HttpException: ${e.message}")
            when (e.code()) {
                429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
                408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
                else -> Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: Exception) {
            Log.d("RemoteDataSource", "Exception: ${e.message}")
            Log.d("RemoteDataSource", "Exception: ${e.printStackTrace()}")
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun getAllRates(): Result<RatesResponse, DataError> {
        return try {
            val response = api.getAllRates()
            if (response.success == true) {
                Log.d("RemoteDataSource", "successfully fetched  all rates: $response")
                Result.Success(response)
            } else {
                Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: IOException) {
            Log.d("RemoteDataSource", " failed to fetch all rates, IOException: ${e.message}")
            Result.Error(DataError.Remote.NO_INTERNET)
        } catch (e: HttpException) {
            when (e.code()) {
                429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
                408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
                else -> Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: Exception) {
            Log.d("RemoteDataSource", "failed to fetch all rates, Exception: ${e.message}")
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun getSymbols(): Result<Symbols, DataError> {
        Log.d("RemoteDataSource", "fetching symbols started")
        return try {
            Log.d("RemoteDataSource", "fetching symbols")
            val response = api.getSymbols()
            Log.d("RemoteDataSource", "successfully fetched symbols: $response")
            if (response.success) {
                Log.d("RemoteDataSource", "successfully fetched symbols: $response")
                Result.Success(response)
            } else {
                Log.d("RemoteDataSource", "failed to fetch symbols: $response")
                Result.Error(DataError.Remote.SERVER)

            }
        } catch (e: IOException) {
            Log.d("RemoteDataSource", "failed to fetch symbols, IOException: ${e.message}")
            Result.Error(DataError.Remote.NO_INTERNET)
        } catch (e: HttpException) {
            Log.d("RemoteDataSource", "failed to fetch symbols, HttpException: ${e.message}")
            when (e.code()) {
                429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
                408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
                else -> Result.Error(DataError.Remote.SERVER)
            }
        }
        catch (e:CancellationException){
            Log.d("RemoteDataSource", "failed to fetch symbols, CancellationException: ${e.message}")
            Result.Error(DataError.Remote.UNKNOWN)
        }
        catch (e: Exception) {
            Log.d("RemoteDataSource", "failed to fetch symbols, Exception: ${e.message}")
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun getConversionCurrencyImage(currency: String): Result<List<FlagResponseItem>, DataError> {
        return try {
            val response = api.getConversionCurrencyImage(currency)
            Log.d("RemoteDataSource", "successfully fetched conversion currency image: $response")
            Result.Success(response)
        } catch (e: IOException) {
            Log.d("RemoteDataSource", "failed to fetch conversion currency image, IOException: ${e.message}")
            Result.Error(DataError.Remote.NO_INTERNET)
        } catch (e: HttpException) {
            Log.d("RemoteDataSource", "failed to fetch conversion currency image, HttpException: ${e.message}")
            when (e.code()) {
                429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
                408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
                else -> Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: Exception) {
            Log.d("RemoteDataSource", "failed to fetch conversion currency image, Exception: ${e.message}")
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
} 