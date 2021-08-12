package com.upar.data.responses

data class ToggleResponse(
    val check: Boolean,
    val checkMsg: String,
    val nope: Boolean,
    val nopeMsg: String,
    val drop: Boolean,
    val dropMsg: String
)