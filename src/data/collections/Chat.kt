package com.upar.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Chat(
    val username: String?="",
    val ign:String?="",
    val clubName:String?="",
    var chat:String?="",
    var date: Long,
    val type:String?=null,
    @BsonId
    val _id:String= ObjectId().toString()
)
data class Wall(
    val username: String? ="",
    val ign:String?="",
    val clubName:String?="",
    val wallOwner:String?="",
    var chat:String?="",
    var date: Long,
    @BsonId
    val _id:String= ObjectId().toString()
)
