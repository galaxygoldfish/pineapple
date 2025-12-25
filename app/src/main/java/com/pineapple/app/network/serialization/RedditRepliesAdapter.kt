package com.pineapple.app.network.serialization

import com.google.gson.*
import com.pineapple.app.network.model.reddit.CommentDataNull
import java.lang.reflect.Type

class RedditRepliesAdapter : JsonDeserializer<CommentDataNull?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): CommentDataNull? {
        // If it's a primitive (like the string ""), return null
        if (json.isJsonPrimitive) {
            return null
        }
        // If it's an object, parse it normally
        return context.deserialize(json, CommentDataNull::class.java)
    }
}
