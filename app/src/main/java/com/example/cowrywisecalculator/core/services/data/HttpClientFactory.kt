package com.example.cowrywisecalculator.core.services.data

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// 1. API Service Provider Enum
enum class ApiProvider(val baseUrl:String){
    FIXER("https://data.fixer.io/api/"),
    EXCHANGE_RATE_API("https://v6.exchangerate-api.com/v6/"),
    ECB("https://data-api.ecb.europa.eu/"),
    REST_COUNTRIES("https://restcountries.com/v3.1/"),
    CUSTOM("")
}



// 2. Network Configuration Data Class
data class NetworkConfig(
    val requiresAuth:Boolean = false,
    val provider: ApiProvider,
    val customBaseUrl: String? = null,
    val apiKey: String? = null,
    val timeout: Long = 30L,
    val enableLogging: Boolean = true,
    val authParam: String? = null
)


// 3. Retrofit Wrapper Class
class RetrofitWrapper  @Inject constructor() {

    companion object {
        @Volatile
        private var INSTANCE: RetrofitWrapper? = null

        fun getInstance(): RetrofitWrapper {
            return INSTANCE
                ?: synchronized(this) {
                INSTANCE
                    ?: RetrofitWrapper()
                        .also { INSTANCE = it }
            }
        }
    }

    private val retrofitInstances = mutableMapOf<String, Retrofit>()

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = false
    }

    private val contentType = "application/json".toMediaType()

    // Create OkHttp client based on configuration
    private fun createOkHttpClient(config: NetworkConfig): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(config.timeout, TimeUnit.SECONDS)
            .readTimeout(config.timeout, TimeUnit.SECONDS)
            .writeTimeout(config.timeout, TimeUnit.SECONDS)

        // Add logging interceptor if enabled
        if (config.enableLogging) {
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                Log.d("HTTP_${config.provider.name}", message)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }

        // Add authentication interceptor if required
        if (config.requiresAuth && !config.apiKey.isNullOrBlank()) {
            val authInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val urlBuilder = originalRequest.url.newBuilder()

                config.authParam?.let { paramName ->
                    urlBuilder.addQueryParameter(paramName, config.apiKey)
                }

                val newRequest = originalRequest.newBuilder()
                    .url(urlBuilder.build())
                    .build()

                chain.proceed(newRequest)
            }
            builder.addInterceptor(authInterceptor)
        }

        return builder.build()
    }

    // Get or create Retrofit instance
    fun <T> getApiService(
        serviceClass: Class<T>,
        config: NetworkConfig
    ): T {
        // Determine base URL based on provider type
        val baseUrl = when (config.provider) {
            ApiProvider.CUSTOM -> config.customBaseUrl
                ?: throw IllegalArgumentException("Custom base URL is required for CUSTOM provider")
            else -> config.provider.baseUrl
        }
        val cacheKey = "${config.provider.name}_${baseUrl}"

        val retrofit = retrofitInstances.getOrPut(cacheKey) {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(createOkHttpClient(config))
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
        }

        return retrofit.create(serviceClass)
    }

    // Convenience method for simple cases
    fun <T> getApiService(
        serviceClass: Class<T>,
        provider: ApiProvider,
        apiKey: String? = null
    ): T {
        val config = NetworkConfig(
            provider = provider,
            apiKey = apiKey
        )
        return getApiService(serviceClass, config)
    }

    // Method for custom URLs
    fun <T> getApiServiceWithCustomUrl(
        serviceClass: Class<T>,
        customUrl: String,
        apiKey: String? = null,
        enableLogging: Boolean = false // Default to false
    ): T {
        val config = NetworkConfig(
            provider = ApiProvider.CUSTOM,
            customBaseUrl = customUrl,
            apiKey = apiKey,
            enableLogging = enableLogging
        )
        return getApiService(serviceClass, config)
    }

    // Clear cache if needed
    fun clearCache() {
        retrofitInstances.clear()
    }
}







