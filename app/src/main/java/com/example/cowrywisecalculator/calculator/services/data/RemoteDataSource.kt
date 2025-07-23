package com.example.cowrywisecalculator.calculator.services.data

import android.util.Log
import androidx.compose.animation.core.KeyframeBaseEntity
import com.example.cowrywisecalculator.calculator.services.data.model.FlagResponse
import com.example.cowrywisecalculator.calculator.services.data.model.FlagResponseItem
import com.example.cowrywisecalculator.calculator.services.data.model.RatesResponse
import com.example.cowrywisecalculator.calculator.services.data.model.Symbols
import com.example.cowrywisecalculator.calculator.domain.DataError
import com.example.cowrywisecalculator.calculator.domain.Result
import com.example.cowrywisecalculator.calculator.domain.onSuccess
import com.example.cowrywisecalculator.core.Default_Base_Currency
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import retrofit2.Response
import safeCall
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "RemoteDataSource"

class RemoteDataSource @Inject constructor(
    @Named("fixer") private val fixerApi: ConversionApi,
    @Named("exchange") private val exchangeRateApi: ConversionApi,
    @Named("countries") private val countriesApi: ConversionApi,
    @Named("exchangeECB") private val exchangeRateECBApi: ConversionApi,

) {



    suspend fun getRates(
        baseCurrency: String,
        conversionCurrency: String
    ): Result<RatesResponse, DataError> {

        //check if base currency is EUR
        if (baseCurrency == Default_Base_Currency) {
            return safeCall {
                val response = exchangeRateECBApi.getECBExchangeRate(conversionCurrency)
                if (response.isSuccessful && response.body() != null) {
                    response
                } else {
                    val response = exchangeRateApi.getExchangeRate(
                        "5fc31d8b0baa235c9bf1d215",
                        baseCurrency,
                        conversionCurrency,
                    )
                    if (response.isSuccessful && response.body() != null) {
                        response
                    } else {
                        return Result.Error(DataError.Remote.UNKNOWN)
                    }


                }
            }


        } else {
            //get the base rate and the conversion rate against EUR
            val baseToEurRate = exchangeRateECBApi.getECBExchangeRate(baseCurrency)
            val conversionToEurRate = exchangeRateECBApi.getECBExchangeRate(conversionCurrency)
            if (baseToEurRate.isSuccessful && conversionToEurRate.isSuccessful && baseToEurRate.body() != null && conversionToEurRate.body() != null) {
                val eBaseRate = baseToEurRate.body()!!.conversionRate
                val eConversionRate = conversionToEurRate.body()!!.conversionRate
                if (eBaseRate !== null && eConversionRate !== null) {
                    val realRate = 1 / eBaseRate * eConversionRate
                    val response = Result.Success(RatesResponse().copy(conversionRate = realRate))
                    return response
                } else {
                    return Result.Error(DataError.Remote.UNKNOWN)
                }
            } else {
             return   safeCall {
                    val rate = exchangeRateApi.getExchangeRate(
                        "5fc31d8b0baa235c9bf1d215",
                        baseCurrency, conversionCurrency
                    )
                    if (rate.isSuccessful && rate.body() != null) {
                        rate
                    } else {
                        return Result.Error(DataError.Remote.UNKNOWN)
                    }
                }
            }


        }
    }



}

