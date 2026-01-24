package com.example.cowrywisecalculator.calculator.services.data.model

import kotlinx.serialization.Serializable

@Serializable

data class FlagResponse(
    val flags: Flags
)

@Serializable
data class Flags(
    val png: String
)
