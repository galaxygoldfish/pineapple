package com.pineapple.app.network.caching.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val name: String,
    val displayName: String? = null,
    val iconUrl: String?,
    val snoovatarUrl: String?,
    val totalKarma: Long? = null,
    val linkKarma: Long? = null,
    val commentKarma: Long? = null,
    val awardeeKarma: Long? = null,
    val awarderKarma: Long? = null,
    val createdUtc: Long? = null
)