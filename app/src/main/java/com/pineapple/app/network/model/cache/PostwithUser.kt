package com.pineapple.app.network.model.cache

import androidx.room.Embedded
import androidx.room.Relation
import com.pineapple.app.network.caching.entity.PostEntity
import com.pineapple.app.network.caching.entity.UserEntity

data class PostWithUser(
    @Embedded val post: PostEntity,
    @Relation(
        parentColumn = "author",
        entityColumn = "name"
    )
    val user: UserEntity?
)
