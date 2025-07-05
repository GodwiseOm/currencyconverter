package com.example.cowrywisecalculator.calculator.services.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FlagResponse(val items:List<com.example.cowrywisecalculator.calculator.services.data.model.FlagResponseItem>)