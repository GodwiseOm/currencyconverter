package com.example.cowrywisecalculator.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Symbols
(
    val success: Boolean,
    val symbols: Map<String, String>
)
