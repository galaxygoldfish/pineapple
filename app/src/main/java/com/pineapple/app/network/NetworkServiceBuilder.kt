package com.pineapple.app.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object NetworkServiceBuilder {

    private const val BASE_URL = "https://api.reddit.com/"

    private val gsonObject = GsonBuilder().setLenient().create()
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    ).build()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gsonObject))
            .build()
    }

    private fun getRetrofitRaw(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    fun apiService(): NetworkService {
        return getRetrofit().create(NetworkService::class.java)
    }

    fun rawApiService(): NetworkService {
        return getRetrofitRaw().create(NetworkService::class.java)
    }


}