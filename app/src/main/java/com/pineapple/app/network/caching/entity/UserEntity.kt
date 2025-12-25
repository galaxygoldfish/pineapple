package com.pineapple.app.network.caching.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val name: String,
    val iconUrl: String?,
    val snoovatarUrl: String?
)