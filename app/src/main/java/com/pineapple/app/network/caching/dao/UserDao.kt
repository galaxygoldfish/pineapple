package com.pineapple.app.network.caching.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pineapple.app.network.caching.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE name = :name")
    suspend fun getUser(name: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)
}