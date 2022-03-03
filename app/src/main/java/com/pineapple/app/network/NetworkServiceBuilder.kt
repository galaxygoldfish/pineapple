package com.pineapple.app.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkServiceBuilder {

    private const val BASE_URL = "https://api.reddit.com/"

    private val gsonObject = GsonBuilder().setLenient().create()
    private val okHttpClient = OkHttpClient.Builder().build()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gsonObject))
            .build()
    }

    fun apiService(): NetworkService {
        return getRetrofit().create(NetworkService::class.java)
    }


}