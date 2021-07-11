package com.upar.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User (
    val username: String,
    val password: String,
    @BsonId
    val _id:String= ObjectId().toString()
)