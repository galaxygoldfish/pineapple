package com.pineapple.app.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object NetworkServiceBuilder {

    const val REDDIT_BASE_URL = "https://www.reddit.com"
    const val OAUTH_BASE_URL = "https://oauth.reddit.com/"
    const val GFYCAT_BASE_URL = "https://api.gfycat.com/v1/gfycats/"

    private val gsonObject = GsonBuilder().setLenient().create()
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    ).build()

    fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gsonObject))
            .build()
    }

    fun getRetrofitRaw(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    inline fun <reified T> apiService(baseUrl: String): T {
        return getRetrofit(baseUrl).create(T::class.java)
    }

    inline fun <reified T> rawApiService(baseUrl: String): T {
        return getRetrofitRaw(baseUrl).create(T::class.java)
    }


}