package com.pineapple.app.di

import android.content.Context
import com.tencent.mmkv.MMKV
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MMKVModule {
    @Provides
    @Singleton
    fun provideMMKV(): MMKV = MMKV.defaultMMKV()
}