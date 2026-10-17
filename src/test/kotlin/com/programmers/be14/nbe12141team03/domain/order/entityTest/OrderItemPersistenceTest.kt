package com.programmers.be14.nbe12141team03.domain.order.entityTest

import com.programmers.be14.nbe12141team03.domain.order.entity.OrderItem
import com.programmers.be14.nbe12141team03.domain.order.entity.OrderResult
import com.programmers.be14.nbe12141team03.domain.product.entity.Product
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.Hibernate
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@Transactional
class OrderItemPersistenceTest {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Test
    @DisplayName("주문 저장 후 재조회해도 단가와 양방향 연결 및 상품 지연 로딩을 유지한다")
    fun persistsOrderItemsAndLazyProductReference() {
        val product = Product("단가 확인", "원두", 5000, null)
        entityManager.persist(product)
        val order = newOrder()
        order.addOrderItem(OrderItem(product, 2000L, 3))
        entityManager.persist(order)
        entityManager.flush()
        val orderId = requireNotNull(order.id)
        val productId = requireNotNull(product.id)
        entityManager.clear()

        val reloadedOrder = entityManager.find(OrderResult::class.java, orderId)
        val reloadedItem = reloadedOrder.orderItemList.single()

        assertThat(reloadedOrder.totalPrice).isEqualTo(6000L)
        assertThat(reloadedItem.orderResult).isSameAs(reloadedOrder)
        assertThat(reloadedItem.orderPrice).isEqualTo(2000L)
        assertThat(reloadedItem.quantity).isEqualTo(3)
        assertThat(reloadedItem.totalPrice()).isEqualTo(6000L)
        assertThat(Hibernate.isInitialized(reloadedItem.product)).isFalse()
        assertThat(requireNotNull(reloadedItem.product).id).isEqualTo(productId)
        assertThat(requireNotNull(reloadedItem.product).name).isEqualTo("단가 확인")
    }

    @Test
    @DisplayName("항목 교체를 저장하면 기존 항목을 삭제하고 새 항목과 합계를 유지한다")
    fun replacingItemsRemovesOrphansAndRecalculatesTotal() {
        val product = Product("교체 확인", "원두", 3000, null)
        entityManager.persist(product)
        val order = newOrder()
        val oldItem = OrderItem(product, 1000L, 2)
        order.addOrderItem(oldItem)
        entityManager.persist(order)
        entityManager.flush()
        val oldItemId = requireNotNull(oldItem.id)
        val orderId = requireNotNull(order.id)

        order.modify("부산", "48000", listOf(OrderItem(product, product.price.toLong(), 3)))
        entityManager.flush()
        entityManager.clear()

        val reloadedOrder = entityManager.find(OrderResult::class.java, orderId)
        val newItem = reloadedOrder.orderItemList.single()
        assertThat(entityManager.find(OrderItem::class.java, oldItemId)).isNull()
        assertThat(reloadedOrder.shippingAddress).isEqualTo("부산")
        assertThat(reloadedOrder.zipCode).isEqualTo("48000")
        assertThat(reloadedOrder.totalPrice).isEqualTo(9000L)
        assertThat(newItem.orderPrice).isEqualTo(3000L)
        assertThat(newItem.quantity).isEqualTo(3)
        assertThat(newItem.orderResult).isSameAs(reloadedOrder)
    }

    private fun newOrder() = OrderResult(
        LocalDateTime.of(2026, 8, 28, 10, 0),
        "test@test.com", "서울", "06234"
    )
}
