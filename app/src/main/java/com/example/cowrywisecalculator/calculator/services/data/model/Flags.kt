package com.example.cowrywisecalculator.calculator.services.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Flags(
    val alt: String,
    val png: String,
    val svg: String
)