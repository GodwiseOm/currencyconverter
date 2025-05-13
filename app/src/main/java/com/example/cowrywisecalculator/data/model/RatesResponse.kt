package com.example.cowrywisecalculator.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RatesResponse(
    val base: String?,
    val date: String?,
    val rates: Map<String, Double>?,
    val success: Boolean?,
    val timestamp: Int?
)