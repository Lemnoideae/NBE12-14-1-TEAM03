package com.programmers.be14.nbe12141team03.domain.order.dto.modify

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class OrderModifyRequest(
    @field:NotBlank(message = "배송지를 입력해 주세요.")
    val shippingAddress: String? = null,

    @field:NotBlank(message = "우편번호를 입력해 주세요.")
    val zipCode: String? = null,

    @field:NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다.")
    @field:Valid
    val orderItemList: List<OrderItemRequest>? = null
) {
    data class OrderItemRequest(
        @field:NotNull(message = "상품 ID를 입력해 주세요.")
        val productId: Long? = null,

        @field:Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        val quantity: Int = 0
    )
}
