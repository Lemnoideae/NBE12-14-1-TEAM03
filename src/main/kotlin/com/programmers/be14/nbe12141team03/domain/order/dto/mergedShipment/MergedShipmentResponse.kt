package com.programmers.be14.nbe12141team03.domain.order.dto.mergedShipment

import java.time.LocalDate

data class MergedShipmentResponse(
    val email: String?,
    val shippingDate: LocalDate?,
    val shippingAddress: String?,
    val zipCode: String?,
    val orderCount: Int,
    val mergedOrderIds: List<Long?>,
    val totalPrice: Long,
    val orderItemList: List<MergedItemResponse>
)
