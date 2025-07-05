package com.example.cowrywisecalculator.calculator.services.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Rates(
    val EUR: Double,
    val GBP: Double,
    val JPY: Double
)