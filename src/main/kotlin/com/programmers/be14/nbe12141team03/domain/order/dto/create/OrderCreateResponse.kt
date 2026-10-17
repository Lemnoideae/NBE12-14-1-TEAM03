package com.programmers.be14.nbe12141team03.domain.order.dto.create

import com.programmers.be14.nbe12141team03.domain.order.entity.OrderResult
import java.time.LocalDate

// 주문 생성 응답 DTO
data class OrderCreateResponse(
    val orderId: Long?,
    val email: String?,
    val totalPrice: Long,
    val shippingDate: LocalDate?
) {
    constructor(orderResult: OrderResult) : this(
        orderResult.id,
        orderResult.email,
        orderResult.totalPrice,
        orderResult.shippingDate
    )
}
