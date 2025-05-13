package com.example.cowrywisecalculator.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FlagResponse(val items:List<FlagResponseItem>)