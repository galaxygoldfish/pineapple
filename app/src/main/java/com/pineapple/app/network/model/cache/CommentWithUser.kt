package com.pineapple.app.network.model.cache

import androidx.room.Embedded
import androidx.room.Relation
import com.pineapple.app.network.caching.entity.CommentEntity
import com.pineapple.app.network.caching.entity.UserEntity

data class CommentWithUser(
    @Embedded val comment: CommentEntity,
    @Relation(
        parentColumn = "author",
        entityColumn = "name"
    )
    val user: UserEntity?
)

