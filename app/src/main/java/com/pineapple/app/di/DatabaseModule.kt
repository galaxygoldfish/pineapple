package com.pineapple.app.di

import android.content.Context
import androidx.room.Room
import com.pineapple.app.network.caching.AppDatabase
import com.pineapple.app.network.caching.dao.PostDao
import com.pineapple.app.network.caching.dao.RemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pineapple-db"
        ).build()
    }

    @Provides
    fun providePostDao(db: AppDatabase): PostDao = db.postDao()

    @Provides
    fun provideRemoteKeyDao(db: AppDatabase): RemoteKeyDao = db.remoteKeyDao()
}
