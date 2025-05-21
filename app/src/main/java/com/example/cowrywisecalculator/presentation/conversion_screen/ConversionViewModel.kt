package com.example.cowrywisecalculator.presentation.conversion_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cowrywisecalculator.data.RetrofitInstance
import com.example.cowrywisecalculator.domain.DataError
import com.example.cowrywisecalculator.domain.Result
import com.example.cowrywisecalculator.domain.onError
import com.example.cowrywisecalculator.domain.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun setBaseAmount(amount: String) {
        _conversionScreenState.update { it.copy(baseAmount = amount) }
    }

    fun setConversionListvisibility() {
        if (!_conversionScreenState.value.conversionListVisibility) {
            _conversionScreenState.update { it.copy(conversionListVisibility = true) }
        } else {
            _conversionScreenState.update { it.copy(conversionListVisibility = false) }

        }
    }


    fun setBaseCurrency(currency: String) {
        _conversionScreenState.update { it.copy(baseCurrency = currency, error = null) }
    }

    fun setConversionCurrency(currency: String) {
        _conversionScreenState.update { it.copy(conversionCurrency = currency, error = null) }
    }

    fun getConversionCurrencyFlag(currency: String) {
        viewModelScope.launch {
            RetrofitInstance.flagRemoteDataSource.getConversionCurrencyImage(currency)
                .onSuccess { data ->
                    data.firstOrNull()?.flags?.png?.let { imageUrl ->
                        _conversionScreenState.update {
                            it.copy(
                                conversionImage = imageUrl,
                                error = null
                            )
                        }
                    }

                }.onError {
                    // Handle error if needed
                }

        }
    }

    fun getBaseCurrencyFlag(currency: String) {
        viewModelScope.launch {
            when (val result =
                RetrofitInstance.flagRemoteDataSource.getConversionCurrencyImage(currency)) {
                is Result.Success -> {
                    result.data.firstOrNull()?.flags?.png?.let { imageUrl ->
                        _conversionScreenState.update {
                            it.copy(
                                baseImage = imageUrl,
                                error = null
                            )
                        }
                    }
                }

                is Result.Error -> {
                    // Handle error if needed
                }
            }
        }
    }


    fun getSymbols() {
        viewModelScope.launch {
            try{
            RetrofitInstance.ratesRemoteDataSource.getSymbols().onSuccess { data ->
                data.symbols.keys.toList().let { symbols ->
                    _conversionScreenState.update { it.copy(symbols = symbols, error = null) }
                }
            }.onError { err ->

                val errorMessage = when (err) {
                    is DataError.Remote -> {
                        when (err) {
                            DataError.Remote.NO_INTERNET -> "No internet connection"
                            DataError.Remote.REQUEST_TIMEOUT -> "Request timed out"
                            DataError.Remote.SERVER -> "Server error"
                            DataError.Remote.TOO_MANY_REQUESTS -> "Too many requests"
                            DataError.Remote.SERIALIZATION -> "Data parsing error"
                            DataError.Remote.UNKNOWN -> "Unknown error occurred"
                        }
                    }

                    is DataError.Local -> {
                        when (err) {
                            DataError.Local.DISK_FULL -> "Storage error"
                            DataError.Local.UNKNOWN -> "Local error occurred"
                        }
                    }
                }
                _conversionScreenState.update { it.copy(error = errorMessage) }
            }
        }
        catch (e: CancellationException) {
            Log.d("ConversionViewModel", " a cancellation exception occured . getSymbols: ${e.message}")
        }
            catch (e: Exception) {
                Log.d("ConversionViewModel", " an exception occured . getSymbols: ${e.message}")
            }
    }}


    fun convert(baseCurrency: String, conversionCurrency: String, baseAmount: String) {
        viewModelScope.launch {
            try {
                // First validate input
                if (baseAmount.isBlank()) {
                    _conversionScreenState.update {
                        it.copy(

                            error = "Please enter a valid amount"
                        )
                    }
                    return@launch
                }

                // Make API call using RemoteDataSource
                when (val result = RetrofitInstance.ratesRemoteDataSource.getRates(
                    baseCurrency,
                    conversionCurrency
                )) {
                    is Result.Success -> {
                        val response = result.data
                        when {
                            response.rates == null -> {
                                _conversionScreenState.update {
                                    it.copy(
                                        conversionAmount = "0.0",
                                        error = "No rates available"
                                    )
                                }
                            }

                            else -> {
                                // Get the conversion rate
                                val rate = response.rates[conversionCurrency]
                                if (rate == null) {
                                    _conversionScreenState.update {
                                        it.copy(
                                            conversionAmount = "0.0",
                                            error = "Rate not found for $conversionCurrency"
                                        )
                                    }
                                } else {
                                    // Calculate and update conversion amount
                                    val convertedAmount = baseAmount.toDouble() * rate
                                    _conversionScreenState.update {
                                        it.copy(
                                            conversionAmount = String.format(
                                                "%.2f",
                                                convertedAmount
                                            ),
                                            error = null
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is Result.Error -> {
                        val errorMessage = when (result.error) {
                            is DataError.Remote -> {
                                when (result.error) {
                                    DataError.Remote.NO_INTERNET -> "No internet connection"
                                    DataError.Remote.REQUEST_TIMEOUT -> "Request timed out"
                                    DataError.Remote.SERVER -> "Server error"
                                    DataError.Remote.TOO_MANY_REQUESTS -> "Too many requests"
                                    DataError.Remote.SERIALIZATION -> "Data parsing error"
                                    DataError.Remote.UNKNOWN -> "Unknown error occurred"
                                }
                            }

                            is DataError.Local -> {
                                when (result.error) {
                                    DataError.Local.DISK_FULL -> "Storage error"
                                    DataError.Local.UNKNOWN -> "Local error occurred"
                                }
                            }
                        }
                        _conversionScreenState.update {
                            it.copy(
                                conversionAmount = "0.0",
                                error = errorMessage
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _conversionScreenState.update {
                    it.copy(
                        conversionAmount = "0.0",
                        error = "An unexpected error occurred"
                    )
                }
            }
        }
    }


}