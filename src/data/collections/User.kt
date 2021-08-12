package com.upar.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User (
    val username: String,
    var password: String,
    var name:String?="",
    var clubName:String?="",
    var ign:String?="",
    var bio:String?="",
    var createdAcc: Long=0,
    var lastActive:Long=0,
    @BsonId
    val _id:String= ObjectId().toString()
)