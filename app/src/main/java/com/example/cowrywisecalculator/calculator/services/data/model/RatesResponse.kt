package com.example.cowrywisecalculator.calculator.services.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RatesResponse(
    val base: String?,
    val date: String?,
    val rates: Map<String, Double>? = null,
    val success: Boolean?,
    val timestamp: Int?,
    @SerialName("base_code")
    val baseCode: String?,
    @SerialName("conversion_rate")
    val conversionRate: Double?,
    @SerialName("conversion_result")
    val conversionResult: Double?,
    val documentation: String?,
    val result: String?,
    @SerialName("target_code")
    val targetCode: String?,
    @SerialName("terms_of_use")
    val termsOfUse: String?,
    @SerialName("time_last_update_unix")
    val timeLastUpdateUnix: Int?,
    @SerialName("time_last_update_utc")
    val timeLastUpdateUtc: String?,
    @SerialName("time_next_update_unix")
    val timeNextUpdateUnix: Int?,
    @SerialName("time_next_update_utc")
    val timeNextUpdateUtc: String?
)