package com.example.flash_currency_converter.calculator.services.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RatesResponse(
    val base: String? = null,
    val date: String? = null,
    val rates: Map<String, Double>? = null,
    val success: Boolean? = null,
    val timestamp: Int? = null,
    @SerialName("base_code")
    val baseCode: String? = null,
    @SerialName("conversion_rate")
    val conversionRate: Double? = null,
    @SerialName("conversion_result")
    val conversionResult: Double? = null,
    val documentation: String? = null,
    val result: String? = null,
    @SerialName("target_code")
    val targetCode: String? = null,
    @SerialName("terms_of_use")
    val termsOfUse: String? = null,
    @SerialName("time_last_update_unix")
    val timeLastUpdateUnix: Int? = null,
    @SerialName("time_last_update_utc")
    val timeLastUpdateUtc: String? = null,
    @SerialName("time_next_update_unix")
    val timeNextUpdateUnix: Int? = null,
    @SerialName("time_next_update_utc")
    val timeNextUpdateUtc: String? = null,
    val dataSets:List<DataSet>? = null,
)




@Serializable
data class DataSet( val series: Map<String, SeriesData>)

@Serializable
data class SeriesData(
    val observations: Map<String, List<Double?>>
)