package com.upar.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Post (
    val title:String,
    val desc:String,
    val date: Long,
    val comment: List<String> =listOf("="),
    @BsonId
    val _id:String= ObjectId().toString()
)