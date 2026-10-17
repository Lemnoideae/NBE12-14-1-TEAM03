package com.programmers.be14.nbe12141team03.domain.order.dto

import com.programmers.be14.nbe12141team03.domain.order.entity.OrderItem

data class OrderItemResponse(
    val productId: Long?,
    val productName: String?,
    val photoUrl: String?,
    val orderPrice: Long,
    val quantity: Int,
    val totalPrice: Long
) {
    constructor(orderItem: OrderItem) : this(
        orderItem.product?.id,
        orderItem.product?.name,
        orderItem.product?.photoUrl,
        orderItem.orderPrice,
        orderItem.quantity,
        orderItem.totalPrice()
    )
}
