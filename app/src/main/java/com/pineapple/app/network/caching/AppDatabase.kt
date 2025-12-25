package com.pineapple.app.network.caching

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pineapple.app.network.caching.dao.PostDao
import com.pineapple.app.network.caching.dao.RemoteKeyDao
import com.pineapple.app.network.caching.dao.SubredditDao
import com.pineapple.app.network.caching.dao.UserDao
import com.pineapple.app.network.caching.dao.SearchResultDao
import com.pineapple.app.network.caching.dao.SearchRemoteKeyDao
import com.pineapple.app.network.caching.dao.CommentDao
import com.pineapple.app.network.caching.entity.PostEntity
import com.pineapple.app.network.caching.entity.RemoteKeyEntity
import com.pineapple.app.network.caching.entity.SubredditEntity
import com.pineapple.app.network.caching.entity.UserEntity
import com.pineapple.app.network.caching.entity.SearchResultEntity
import com.pineapple.app.network.caching.entity.SearchRemoteKeyEntity
import com.pineapple.app.network.caching.entity.CommentEntity

@Database(
    entities = [
        PostEntity::class,
        RemoteKeyEntity::class,
        UserEntity::class,
        SubredditEntity::class,
        SearchResultEntity::class,
        SearchRemoteKeyEntity::class,
        CommentEntity::class
    ],
    version = 6
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun userDao(): UserDao
    abstract fun subredditDao(): SubredditDao
    abstract fun searchResultDao(): SearchResultDao
    abstract fun searchRemoteKeyDao(): SearchRemoteKeyDao
    abstract fun commentDao(): CommentDao
}