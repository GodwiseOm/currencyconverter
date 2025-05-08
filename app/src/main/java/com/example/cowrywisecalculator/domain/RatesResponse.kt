package com.example.cowrywisecalculator.domain

import com.example.cowrywisecalculator.domain.model.Rates

data class RatesResponse(
    val base: String?,
    val date: String?,
    val rates: Map<String, Double>?,
    val success: Boolean?,
    val timestamp: Int?
)