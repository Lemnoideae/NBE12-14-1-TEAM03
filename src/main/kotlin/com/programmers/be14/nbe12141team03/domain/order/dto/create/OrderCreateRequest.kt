package com.programmers.be14.nbe12141team03.domain.order.dto.create

import com.programmers.be14.nbe12141team03.domain.order.dto.OrderItemRequest
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern

data class OrderCreateRequest(
    @field:Email(message = "올바른 Email의 형식이 아닙니다.")
    @field:NotBlank(message = "Email를 입력해주세요.")
    val email: String? = null,

    @field:NotEmpty(message = "구매 상품을 하나 이상 선택해주세요.")
    val items: List<OrderItemRequest>? = null,

    @field:NotBlank(message = "배송지를 입력해주세요.")
    val shippingAddress: String? = null,

    @field:NotBlank(message = "우편 번호를 입력해주세요.")
    @field:Pattern(regexp = "^\\d{5}$", message = "우편 번호는 5자리 숫자여야 합니다.")
    val zipCode: String? = null
)
