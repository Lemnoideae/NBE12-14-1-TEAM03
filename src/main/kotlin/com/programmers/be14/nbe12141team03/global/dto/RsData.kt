package com.programmers.be14.nbe12141team03.global.dto

data class RsData<T>(
    val resultCode : String,
    val msg : String,
    val data : T? = null
) {
    val statusCode = resultCode.split("-")[0].toInt()
}
