package com.programmers.be14.nbe12141team03.domain.order.dto

import com.programmers.be14.nbe12141team03.domain.order.dto.create.OrderCreateRequest
import com.programmers.be14.nbe12141team03.domain.order.dto.modify.OrderModifyRequest
import jakarta.validation.Validation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrderRequestValidationTest {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `order creation constraints are visible on Kotlin fields`() {
        val request = OrderCreateRequest(
            email = "not-an-email",
            items = emptyList(),
            shippingAddress = " ",
            zipCode = "123"
        )

        val paths = validator.validate(request).map { it.propertyPath.toString() }.toSet()

        assertThat(paths).containsExactlyInAnyOrder("email", "items", "shippingAddress", "zipCode")
    }

    @Test
    fun `order modification cascades validation to nested items`() {
        val request = OrderModifyRequest(
            shippingAddress = "서울",
            zipCode = "06234",
            orderItemList = listOf(OrderModifyRequest.OrderItemRequest(productId = null, quantity = 0))
        )

        val paths = validator.validate(request).map { it.propertyPath.toString() }.toSet()

        assertThat(paths).containsExactlyInAnyOrder(
            "orderItemList[0].productId",
            "orderItemList[0].quantity"
        )
    }
}
