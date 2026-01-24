package com.example.cowrywisecalculator.calculator.presentation.conversion_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cowrywisecalculator.calculator.domain.CalculatorRepository

import com.example.cowrywisecalculator.calculator.domain.DataError
import com.example.cowrywisecalculator.calculator.domain.onError
import com.example.cowrywisecalculator.calculator.domain.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ConversionViewModel"

@HiltViewModel
class ConversionViewModel @Inject constructor(
    private val calculatorRepository: CalculatorRepository
) : ViewModel() {
    var convertJob: Job? = null

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
        Log.d(TAG, "setBaseAmount: $amount")
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

    fun getFlag(currency: String, conversionImage: Boolean) {
        viewModelScope.launch {
            calculatorRepository.getConversionCurrencyImage(currency)
                .onSuccess { data ->
                    data.firstOrNull()?.flags?.png?.let { imageUrl ->
                        _conversionScreenState.update {
                            if (conversionImage) {
                                it.copy(
                                    conversionImage = imageUrl,
                                    error = null
                                )
                            } else {
                                it.copy(baseImage = imageUrl, error = null)

                            }
                        }
                    }

                }.onError {
                    // Handle error if needed
                }

        }
    }


    private fun getSymbols() {
        viewModelScope.launch {
            try {
                calculatorRepository.getSymbols().onSuccess { data ->
                    data.symbols.let { symbols ->
                        Log.d(TAG, "successfully fetched symbols")
                        _conversionScreenState.update { it.copy(symbols = symbols, error = null) }
                    }
                }.onError { err ->
                    Log.d(TAG,"failed to get symbols. error is $err")

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
            } catch (e: CancellationException) {
                Log.d(
                    "ConversionViewModel",
                    " a cancellation exception occured . getSymbols: ${e.message}"
                )
            } catch (e: Exception) {
                Log.d("ConversionViewModel", " an exception occured . getSymbols: ${e.message}")
            }
        }
    }


    fun convert(baseCurrency: String, conversionCurrency: String, baseAmount: String) {
        convertJob?.cancel()
        convertJob = viewModelScope.launch {

            calculatorRepository.getRates(
                baseCurrency,
                conversionCurrency
            ).onSuccess { data ->
                Log.d("ConversionViewModel", "successfully fetched conversion rate")
                // Get the conversion rate
                val rate = data.rates?.get(conversionCurrency)
                    ?: data.dataSets?.firstOrNull()?.series?.values?.firstOrNull()?.observations?.values?.firstOrNull()
                        ?.firstOrNull() ?: data.conversionRate

                Log.d(TAG, "rate is $rate")
                if (rate == null) {
                    Log.d(TAG, "rate is null")

                    _conversionScreenState.update {
                        it.copy(
                            conversionAmount = "0.0",
                            error = "Rate not found for $conversionCurrency"
                        )
                    }
                } else {
                    Log.d(
                        "ConversionViewModel",
                        "converting base to other currency, rate is $rate"
                    )
                    // Calculate and update conversion amount
                    val convertedAmount = baseAmount.toDouble() * rate
                    _conversionScreenState.update {
                        Log.d(TAG, "conversion screen updated: $convertedAmount")
                        it.copy(
                            conversionAmount = convertedAmount.toString(),
                            error = null
                        )
                    }
                }
            }.onError { err ->
                Log.d(
                    "ConversionViewModel",
                    "an error occurred while fetching default conversion rate, $err"
                )
                _conversionScreenState.update {
                    it.copy(error = "An error occurred while converting")
                }

            }

        }
    }
}


