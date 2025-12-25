package com.pineapple.app.di

import com.google.gson.GsonBuilder
import com.pineapple.app.network.interceptor.AuthInterceptor
import com.pineapple.app.network.api.AuthRetrofit
import com.pineapple.app.network.api.RedditApi
import com.pineapple.app.network.repository.RedditRepository
import com.pineapple.app.network.api.RedditTokenApi
import com.pineapple.app.network.api.TokenRetrofit
import com.pineapple.app.network.interceptor.TokenUserAgentInterceptor
import com.pineapple.app.network.model.reddit.CommentDataNull
import com.pineapple.app.network.repository.RedditAuthRepository
import com.pineapple.app.network.serialization.RedditRepliesAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        repository: RedditAuthRepository
    ): AuthInterceptor = AuthInterceptor(repository)

    @Provides
    @Singleton
    fun provideTokenUaInterceptor(): TokenUserAgentInterceptor =
        TokenUserAgentInterceptor()

    @AuthRetrofit
    @Provides
    @Singleton
    fun provideOAuthOkHttpClient(
        logging: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .build()

    @TokenRetrofit
    @Provides
    @Singleton
    fun provideTokenOkHttpClient(
        logging: HttpLoggingInterceptor,
        tokenUaInterceptor: TokenUserAgentInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(tokenUaInterceptor)
        .build()

    @AuthRetrofit
    @Provides
    @Singleton
    fun provideOAuthRetrofit(
        @AuthRetrofit okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://oauth.reddit.com/")
        .client(okHttpClient)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .registerTypeAdapter(CommentDataNull::class.java, RedditRepliesAdapter())
                    .create()
            )
        )
        .build()

    @TokenRetrofit
    @Provides
    @Singleton
    fun provideTokenRetrofit(
        @TokenRetrofit okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://www.reddit.com/")
        .client(okHttpClient)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .registerTypeAdapter(CommentDataNull::class.java, RedditRepliesAdapter())
                    .create()
            )
        )
        .build()

    @Provides
    @Singleton
    fun provideRedditApi(
        @AuthRetrofit oauthRetrofit: Retrofit
    ): RedditApi = oauthRetrofit.create(RedditApi::class.java)

    @Provides
    @Singleton
    fun provideTokenApi(
        @TokenRetrofit tokenRetrofit: Retrofit
    ): RedditTokenApi = tokenRetrofit.create(RedditTokenApi::class.java)

}