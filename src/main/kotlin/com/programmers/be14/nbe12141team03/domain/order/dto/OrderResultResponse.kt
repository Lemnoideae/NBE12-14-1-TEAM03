package com.programmers.be14.nbe12141team03.domain.order.dto

import com.programmers.be14.nbe12141team03.domain.order.entity.OrderResult
import java.time.LocalDate
import java.time.LocalDateTime

data class OrderResultResponse(
    val id: Long?,
    val createDate: LocalDateTime?,
    val modifyDate: LocalDateTime?,
    val email: String?,
    val shippingAddress: String?,
    val zipCode: String?,
    val totalPrice: Long,
    val shippingDate: LocalDate?,
    val orderItemList: List<OrderItemResponse>,
    val modifiable: Boolean
) {
    constructor(orderResult: OrderResult) : this(
        orderResult.id,
        orderResult.createDate,
        orderResult.modifyDate,
        orderResult.email,
        orderResult.shippingAddress,
        orderResult.zipCode,
        orderResult.totalPrice,
        orderResult.shippingDate,
        orderResult.orderItemList.map(::OrderItemResponse),
        orderResult.isModifiable(LocalDateTime.now())
    )
}
