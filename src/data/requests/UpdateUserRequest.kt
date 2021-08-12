package com.upar.data.requests

data class UpdateUserRequest(
    val name:String?="",
    val clubName:String?="",
    val ign:String?="",
    val bio:String?=""
)
