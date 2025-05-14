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
    const val flagUrl = "https://restcountries.com/v3.1/currency/"

    val contentType = "application/json".toMediaType()

    // api to fetch flag
    private val conversionApiflag: ConversionApi by lazy {
        Retrofit.Builder().baseUrl(flagUrl)
            .addConverterFactory(json.asConverterFactory(contentType)).build()
            .create(ConversionApi::class.java)
    }

    //generic api to fetch rates
    private val conversionApiImp: ConversionApi by lazy {
        Retrofit.Builder().baseUrl("https://data.fixer.io/api/")
            .addConverterFactory(json.asConverterFactory(contentType)).build()
            .create(ConversionApi::class.java)
    }

    // Remote data sources
    val flagRemoteDataSource: RemoteDataSource by lazy {
        RemoteDataSource(conversionApiflag)
    }

    val ratesRemoteDataSource: RemoteDataSource by lazy {
        RemoteDataSource(conversionApiImp)
    }
}