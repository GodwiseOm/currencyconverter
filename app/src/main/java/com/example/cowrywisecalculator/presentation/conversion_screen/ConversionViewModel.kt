package com.example.cowrywisecalculator.presentation.conversion_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cowrywisecalculator.data.RetrofitInstance
import com.example.cowrywisecalculator.data.model.RatesResponse
import com.example.cowrywisecalculator.data.model.Symbols
import com.example.cowrywisecalculator.domain.DataError
import com.example.cowrywisecalculator.domain.Result
import com.example.cowrywisecalculator.domain.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
private const val TAG = "conversionViewModel"

@HiltViewModel
class ConversionViewModel @Inject constructor() : ViewModel() {

    init {
        getSymbols()
    }

    //ui state for conversion screen
    private val _conversionScreenState: MutableStateFlow<ConversionScreenState> =
        MutableStateFlow(ConversionScreenState())

    val conversionScreenState: StateFlow<ConversionScreenState> =
        _conversionScreenState.asStateFlow()


    fun setBaseListvisibility() {
        if (!_conversionScreenState.value.baseListVisibility) {
            _conversionScreenState.update { it.copy(baseListVisibility = true) }
        } else {
            _conversionScreenState.update { it.copy(baseListVisibility = false) }

        }
    }
fun setBaseAmount(amount:String){
    _conversionScreenState.update { it.copy(baseAmount = amount) }
}
    fun setConversionListvisibility() {
        if (!_conversionScreenState.value.conversionListVisibility) {
            _conversionScreenState.update { it.copy(conversionListVisibility = true) }
        } else {
            _conversionScreenState.update { it.copy(conversionListVisibility = false) }

        }
    }


    fun getCurrencyList() {
        viewModelScope.launch {
            val rates: RatesResponse? = try {
                RetrofitInstance.conversionApiImp.getAllRates()
            } catch (e: IOException) {
                (RatesResponse(null, null, emptyMap(), null, 0))
            } catch (e: HttpException) {
                (RatesResponse(null, null, emptyMap(), null, 0))
            }
            if (rates?.success == true) {
                _conversionScreenState.update {it.copy(allCurrency = rates)  }
            }

        }
    }


    fun setBaseCurrency(currency: String) {
        _conversionScreenState.update { it.copy(baseCurrency = currency) }
        getBaseCurrencyFlag(currency)
    }

    fun setConversionCurrency(currency: String) {
        _conversionScreenState.update { it.copy(conversionCurrency = currency) }
        getConversionCurrencyFlag(currency)
    }

    fun getConversionCurrencyFlag(currency: String) {
        viewModelScope.launch {
            Log.d(TAG, "getConversionCurrencyFlag: called for $currency")
            val image: String? = try {
                RetrofitInstance.conversionApiflag.getConversionCurrencyImage(currency).items[0].flags.png

            } catch (e: IOException) {
                Log.d(TAG, "io exception, failed to fetch flag response was  ${e.printStackTrace()}")
                null
            } catch (e: HttpException) {
                Log.d(TAG, "http exception, faied to fetch flag response was  ${e.printStackTrace()}")
                null
            } catch (e: Exception) {
                Log.d(TAG, "failed with exception, fetching flag response was  ${e.cause}")
                Log.d(TAG, "failed with exception, fetching flag response was  ${e.printStackTrace()}")
                null

            }
            if (image?.isEmpty() == false) {
               _conversionScreenState.update { it.copy(conversionImage = image) }
            }
        }
    }

    fun getBaseCurrencyFlag(currency: String) {

        viewModelScope.launch {
            Log.d(TAG, "getBaseCurrencyFlag: called for [$currency] ")
            val image: String? = try {
                RetrofitInstance.conversionApiflag.getConversionCurrencyImage(currency).items[0].flags.png
            } catch (e: IOException) {
                null
            } catch (e: HttpException) {
                null
            } catch (e: Exception) {
                null
            }
            if (image?.isEmpty() == false) {
               _conversionScreenState.update { it.copy(baseImage = image) }
            }
        }
    }


    fun getSymbols(){
        viewModelScope.launch {
            val symbols: Symbols? = try {
                RetrofitInstance.conversionApiImp.getSymbols()
            } catch (e: IOException) {
                null
            } catch (e: HttpException) {
                null
            }
            //update uistate to reflect symbols
            symbols?.symbols?.let { allSymbols ->
                _conversionScreenState.update { it.copy(symbols = allSymbols.keys.toList()) }
            }
        }
    }
    fun convert(baseCurrency: String, conversionCurrency: String, baseAmount: String) {
        viewModelScope.launch {
            val response:Result<RatesResponse,DataError> =
                RetrofitInstance.conversionApiImp.getRates(baseCurrency, conversionCurrency)
            response.onSuccess {
                data ->
                val rate = data.rates?.get(conversionCurrency)?.times(baseAmount.toDouble())
                _conversionScreenState.update { it.copy(conversionAmount = ) }
            }
//             try {
//
//
//            } catch (e: IOException) {
//                e.printStackTrace()
//                null
//            } catch (e: HttpException) {
//                e.printStackTrace()
//                null
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//            // get the actual conversion rate
//            rates?.rates?.let { rate ->
//                val conversionRate = rate[conversionCurrency]
//                _conversionScreenState.update { it.copy(conversionAmount = (baseAmount.toDouble() * conversionRate!!).toString()) }
//            }
        }

    }}


}