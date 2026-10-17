package com.programmers.be14.nbe12141team03.global.exception

import com.programmers.be14.nbe12141team03.global.dto.RsData

class ApiServiceException(
    val resultCode: String,
    msg: String
) : RuntimeException(msg) {

    val rsData = RsData<Nothing>(resultCode, msg)
}
