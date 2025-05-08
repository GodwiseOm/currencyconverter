package com.example.cowrywisecalculator.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitInstance {
    val json = Json {
        ignoreUnknownKeys = true // prevents crashes on unexpected JSON fields
    }

    // api to fetch flags
    const val flagUrl = "https://restcountries.com/v3.1/"

    val contentType = "application/json".toMediaType()

    // api to fetch flag
    val conversionApiflag: ConversionApi by lazy {
        Retrofit.Builder().baseUrl(flagUrl)
            .addConverterFactory(json.asConverterFactory(contentType)).build()
            .create(ConversionApi::class.java)
    }

    //generic api to fetch rates
    val conversionApiImp: ConversionApi by lazy {

        Retrofit.Builder().baseUrl("https://data.fixer.io/api/")
            .addConverterFactory(json.asConverterFactory(contentType)).build()
            .create(ConversionApi::class.java)
    }

}