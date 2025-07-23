package com.example.cowrywisecalculator.calculator.presentation.conversion_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cowrywisecalculator.calculator.domain.CalculatorRepository
import com.example.cowrywisecalculator.core.Default_Base_Currency

import com.example.cowrywisecalculator.calculator.domain.DataError
import com.example.cowrywisecalculator.calculator.domain.Result
import com.example.cowrywisecalculator.calculator.domain.onError
import com.example.cowrywisecalculator.calculator.domain.onSuccess
import com.example.cowrywisecalculator.calculator.services.data.RemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
private const val TAG = "ConversionViewModel"
@HiltViewModel
class ConversionViewModel @Inject constructor(
   private val  calculatorRepository: CalculatorRepository
) : ViewModel() {

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

    fun getConversionCurrencyFlag(currency: String) {
        viewModelScope.launch {
            calculatorRepository.getConversionCurrencyImage(currency)
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
                calculatorRepository.getConversionCurrencyImage(currency)) {
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
            try { calculatorRepository.getSymbols().onSuccess { data ->
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
        viewModelScope.launch {


            if (baseCurrency == Default_Base_Currency) {
                calculatorRepository.getRates(
                    baseCurrency,
                    conversionCurrency
                ).onSuccess { data ->
                    Log.d("ConversionViewModel", "successfully fetched conversion rate")
                    // Get the conversion rate
                    val rate = data.rates?.get(conversionCurrency)?:data.conversionRate
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
            } else {
                //convert the base amount to be in the default currency
                calculatorRepository.getRates(
                    Default_Base_Currency, baseCurrency
                ).onSuccess { data ->
                    Log.d("ConversionViewModel", "successfully fetched default conversion/base rate")
                    //do rates x base currency
                    val rate = data.rates?.get(baseCurrency)?:data.conversionRate
                    Log.d(TAG, "rate = $rate")
                    if (rate == null) {
                        _conversionScreenState.update {
                            it.copy(
                                conversionAmount = "0.0",
                                error = "Rate not found for $conversionCurrency"
                            )
                        }
                        return@launch
                    } else {

                        Log.d(
                            "ConversionViewModel",
                            " default/ base rate is $rate"
                        )
                        val euroAmount = baseAmount.toDouble() / rate
                       calculatorRepository.getRates(
                            Default_Base_Currency, conversionCurrency
                        ).onSuccess { newData ->
                            Log.d(
                                "ConversionViewModel",
                                "successfully fetched default/conversion rate"
                            )
                            //cCRate is the default/conversion currency rate
                            val cCRate = newData.rates?.get(conversionCurrency)?:newData.conversionRate
                            if (cCRate == null) {
                                Log.d(TAG, "unable to get cCrate")
                                _conversionScreenState.update {
                                    it.copy(
                                        conversionAmount = "0.0",
                                        error = "Rate not found for $conversionCurrency"
                                    )
                                }
                                return@launch
                            } else {



                                //fetch the rate for default currency/conversion currency
                                val convertedAmount = euroAmount * rate

                                _conversionScreenState.update {
                                    it.copy(
                                        conversionAmount = convertedAmount.toString(),
                                        error = null
                                    )
                                }
                            }


                        }.onError { err ->
                            Log.d(
                                "ConversionViewModel",
                                "an error occurred while fetching default / conversion rate, $err"
                            )
                            _conversionScreenState.update {
                                it.copy(error = "An error occurred while converting")
                            }
                        }


                    }.onError { err ->
                        Log.d(
                            "ConversionViewModel",
                            "an error occurred while fetching conversion rate, $err"
                        )
                        _conversionScreenState.update {
                            it.copy(error = "An error occurred while converting")
                        }
                    }
                }.onError {err->
                    Log.d(
                        "ConversionViewModel",
                        "an error occurred while fetching default / base conversion rate, $err"
                    )
                    _conversionScreenState.update {
                        it.copy(error = "An error occurred while converting")

                    }

                }

            }
        }
    }
}


