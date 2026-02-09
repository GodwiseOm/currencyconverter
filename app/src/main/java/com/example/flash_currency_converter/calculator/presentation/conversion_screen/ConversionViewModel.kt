package com.example.flash_currency_converter.calculator.presentation.conversion_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flash_currency_converter.calculator.domain.CalculatorRepository

import com.example.flash_currency_converter.calculator.domain.DataError
import com.example.flash_currency_converter.calculator.domain.onError
import com.example.flash_currency_converter.calculator.domain.onSuccess
import com.example.flash_currency_converter.calculator.utils.formatAsMoney
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ConversionViewModel"

@HiltViewModel
class ConversionViewModel @Inject constructor(
    private val calculatorRepository: CalculatorRepository
) : ViewModel() {


    var convertJob: Job? = null
    var showSplashScreen = MutableStateFlow(true)

    init {
        getSymbols()

    }

    //ui state for conversion screen
    private val _conversionScreenState: MutableStateFlow<ConversionScreenState> =
        MutableStateFlow(ConversionScreenState())

    val conversionScreenState: StateFlow<ConversionScreenState> =
        _conversionScreenState.asStateFlow()

    // UI Events Channel - THE KEY SETUP
    private val _conversionScreenEvents = Channel<ConversionScreenEvent>()
    val conversionScreenEvents = _conversionScreenEvents.receiveAsFlow()


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
        _conversionScreenState.update { it.copy(baseCurrency = currency,  conversionAmount = "",error = null) }
    }

    fun setConversionCurrency(currency: String) {
        _conversionScreenState.update { it.copy(conversionCurrency = currency, conversionAmount = "", error = null) }
    }

    fun getFlag(currency: String, conversionImage: Boolean) {
        when (Pair(currency, conversionImage)) {
            Pair("EUR", true) -> {
                _conversionScreenState.update { it.copy(conversionImage = "https://flagcdn.com/w320/eu.png") }
                return
            }

            Pair("USD", false) -> {
                _conversionScreenState.update { it.copy(baseImage = "https://flagcdn.com/w320/us.png") }
                return
            }

            Pair("USD", true) -> {
                _conversionScreenState.update { it.copy(conversionImage = "https://flagcdn.com/w320/us.png") }
                return
            }

            Pair("EUR", false) -> {
                // Handle other cases
                _conversionScreenState.update { it.copy(baseImage = "https://flagcdn.com/w320/eu.png") }
                return
            }
        }
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

                }.onError { err ->

                    Log.d(TAG, "failed to get symbols. error is $err")

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
                    _conversionScreenState.update {
                        if (conversionImage) {
                            _conversionScreenEvents.send(ConversionScreenEvent.showSnackBar(errorMessage))
                            it.copy( conversionImage = null)

                        } else {
                            _conversionScreenEvents.send(ConversionScreenEvent.showSnackBar(errorMessage))
                            it.copy( baseImage = null)
                        }
                    }
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
                    Log.d(TAG, "failed to get symbols. error is $err")

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
                    _conversionScreenEvents.send(ConversionScreenEvent.showSnackBar(errorMessage))

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
            Log.d(TAG, "converting $baseAmount $baseCurrency to $conversionCurrency")
            _conversionScreenState.update { it.copy(showLoading = true) }

            calculatorRepository.getRates(
                baseCurrency,
                conversionCurrency
            ).onSuccess {

                    data ->
                _conversionScreenState.update { it.copy(showLoading = false) }
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
                    val amountInText = convertedAmount.formatAsMoney()
                    _conversionScreenState.update {
                        Log.d(TAG, "conversion screen updated: $convertedAmount")
                        it.copy(
                            conversionAmount = amountInText,
                            error = null
                        )
                    }
                }
            }.onError { err ->
                val error = when (err) {
                    DataError.Remote.REQUEST_TIMEOUT -> "The request timed out. Please try again."
                    DataError.Remote.TOO_MANY_REQUESTS -> "Too many requests. Please wait a moment."
                    DataError.Remote.NO_INTERNET -> "No internet connection. Check your network settings."
                    DataError.Remote.SERVER -> "Please try again later."
                    DataError.Remote.SERIALIZATION -> "There was an error processing the data."
                    DataError.Remote.UNKNOWN -> "An unknown error occurred."

                    DataError.Local.DISK_FULL -> "Your device storage is full. Free up space and try again."
                    DataError.Local.UNKNOWN -> "An unknown local error occurred."
                    else -> "An unknown error occurred, you can try again"
                }
                _conversionScreenState.update { it.copy(showLoading = false, conversionAmount = "0.0") }
                Log.d(
                    "ConversionViewModel",
                    "an error occurred while fetching default conversion rate, $err"
                )
                _conversionScreenEvents.send(ConversionScreenEvent.showSnackBar(error))

            }

        }
    }
}


