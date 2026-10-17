package com.programmers.be14.nbe12141team03.domain.order.entityTest

import com.programmers.be14.nbe12141team03.domain.order.entity.OrderItem
import com.programmers.be14.nbe12141team03.domain.order.entity.OrderResult
import com.programmers.be14.nbe12141team03.domain.product.entity.Product
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class OrderResultTest {
    @Test
    @DisplayName("14:00 이전 주문은 당일 배송")
    fun t1() {
        val order = OrderResult(
            LocalDateTime.of(2026, 8, 28, 13, 59, 59),
            "test@test.com", "서울", "06234"
        )

        assertThat(order.shippingDate).isEqualTo(LocalDate.of(2026, 8, 28))
    }

    @Test
    @DisplayName("14:00 정각 주문은 다음 날 배송")
    fun t2() {
        val order = OrderResult(
            LocalDateTime.of(2026, 8, 28, 14, 0),
            "test@test.com", "서울", "06234"
        )

        assertThat(order.shippingDate).isEqualTo(LocalDate.of(2026, 8, 29))
    }

    @Test
    @DisplayName("항목 추가는 주문 역참조와 총 금액을 함께 갱신한다")
    fun addOrderItemUpdatesListBackReferenceAndTotalPrice() {
        val order = newOrder()
        val first = OrderItem(Product("Coffee A", "원두", 2000, null), 2000L, 2)
        val second = OrderItem(Product("Coffee B", "원두", 3000, null), 3000L, 1)

        order.addOrderItem(first)
        order.addOrderItem(second)

        assertThat(order.orderItemList).containsExactly(first, second)
        assertThat(first.orderResult).isSameAs(order)
        assertThat(second.orderResult).isSameAs(order)
        assertThat(order.totalPrice).isEqualTo(7000L)
    }

    @Test
    @DisplayName("항목 합계는 상품의 현재 가격 대신 주문 당시 단가를 사용한다")
    fun totalPriceUsesOrderPrice() {
        val product = Product("Coffee", "원두", 5000, null)
        val item = OrderItem(product, 2000L, 3)

        assertThat(item.totalPrice()).isEqualTo(6000L)
    }

    @Test
    @DisplayName("항목 금액의 곱셈 오버플로를 거부한다")
    fun totalPriceRejectsMultiplicationOverflow() {
        val item = OrderItem(Product("Coffee", "원두", 1000, null), Long.MAX_VALUE, 2)

        assertThatThrownBy { item.totalPrice() }.isInstanceOf(ArithmeticException::class.java)
    }

    @Test
    @DisplayName("주문 합계의 덧셈 오버플로를 거부한다")
    fun addOrderItemRejectsAdditionOverflow() {
        val order = newOrder()
        val product = Product("Coffee", "원두", 1000, null)
        order.addOrderItem(OrderItem(product, Long.MAX_VALUE, 1))

        assertThatThrownBy { order.addOrderItem(OrderItem(product, 1L, 1)) }
            .isInstanceOf(ArithmeticException::class.java)
    }

    @Test
    @DisplayName("익일 배송 주문도 배송일 14:00부터 수정할 수 없다")
    fun isModifiableUntilShippingDayCutoff() {
        val order = OrderResult(
            LocalDateTime.of(2026, 8, 28, 14, 0),
            "test@test.com", "서울", "06234"
        )

        assertThat(order.isModifiable(LocalDateTime.of(2026, 8, 29, 13, 59, 59))).isTrue()
        assertThat(order.isModifiable(LocalDateTime.of(2026, 8, 29, 14, 0))).isFalse()
        assertThat(order.isModifiable(LocalDateTime.of(2026, 8, 29, 14, 0, 1))).isFalse()
    }

    private fun newOrder() = OrderResult(
        LocalDateTime.of(2026, 8, 28, 10, 0),
        "test@test.com", "서울", "06234"
    )
}
