package com.programmers.be14.nbe12141team03.domain.order.dto.mergedShipment

data class MergedItemResponse(
    val productId: Long?,
    val productName: String?,
    val photoUrl: String?,
    val quantity: Int,
    val totalPrice: Long
)
