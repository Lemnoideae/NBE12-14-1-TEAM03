package com.programmers.be14.nbe12141team03.domain.order.service

import com.jayway.jsonpath.JsonPath
import com.programmers.be14.nbe12141team03.domain.order.dto.mergedShipment.MergedShipmentResponse
import com.programmers.be14.nbe12141team03.domain.order.entity.OrderItem
import com.programmers.be14.nbe12141team03.domain.order.entity.OrderResult
import com.programmers.be14.nbe12141team03.domain.order.repository.OrderResultRepository
import com.programmers.be14.nbe12141team03.domain.product.entity.Product
import com.programmers.be14.nbe12141team03.domain.product.repository.ProductRepository
import com.programmers.be14.nbe12141team03.global.exception.ApiServiceException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate

@SpringBootTest
class OrderResultServiceTest {

    @Autowired
    private lateinit var service: OrderResultService

    @Autowired
    private lateinit var orderResultRepository: OrderResultRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    private lateinit var entityManager: jakarta.persistence.EntityManager

    @Test
    fun createModifyAndDeleteOrderThroughApi() {
        val product = productRepository.save(Product("Order test coffee", "커피콩", 1250, null))
        val mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        val createRequest = """
                {"email":"order-test@example.com","shippingAddress":"address-a","zipCode":"06234",
                 "items":[{"productId":${product.id},"quantity":2}]}
                """.trimIndent()

        val created = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON).content(createRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.data.totalPrice").value(2500))
                .andReturn().response.contentAsString
        val orderId = (JsonPath.read(created, "$.data.orderId") as Number).toLong()

        val modifyRequest = """
                {"shippingAddress":"address-b","zipCode":"06235",
                 "orderItemList":[{"productId":%d,"quantity":3}]}
                """.format(product.id)
        mockMvc.perform(patch("/api/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON).content(modifyRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.shippingAddress").value("address-b"))
                .andExpect(jsonPath("$.data.totalPrice").value(3750))
                .andExpect(jsonPath("$.data.orderItemList[0].quantity").value(3))

        mockMvc.perform(delete("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
        assertThat(orderResultRepository.existsById(orderId)).isFalse()
    }

    @Test
    fun mergedShipmentsGroupByEmailAndAddressAndSumItems() {
        val shippingDate = LocalDate.of(2098, 2, 3)
        val product = productRepository.save(Product("Test coffee", "커피콩", 1250, null))
        val first = order(shippingDate, "address-a", product, 2)
        val second = order(shippingDate, "address-a", product, 3)
        val otherAddress = order(shippingDate, "address-b", product, 1)
        val otherZipCode = order(shippingDate, "address-a", product, 1, zipCode = "99999")
        val otherEmail = order(
            shippingDate,
            "address-a",
            product,
            1,
            email = "another@example.com"
        )
        orderResultRepository.saveAll(listOf(first, second, otherAddress, otherZipCode, otherEmail))

        val shipments = service.getMergedByShippingDate(shippingDate)

        assertThat(shipments).hasSize(4)
        val merged = shipments.stream()
                .filter {
                    it.email == "merge-test@example.com" &&
                        it.shippingAddress == "address-a" &&
                        it.zipCode == "06234"
                }
                .findFirst()
                .orElseThrow()
        assertThat(merged.orderCount).isEqualTo(2)
        assertThat(merged.mergedOrderIds).containsExactlyInAnyOrder(first.id, second.id)
        assertThat(merged.totalPrice).isEqualTo(6250L)
        assertThat(merged.orderItemList).hasSize(1)
        assertThat(merged.orderItemList[0].quantity).isEqualTo(5)
        assertThat(merged.orderItemList[0].totalPrice).isEqualTo(6250L)

        val separate = shipments.stream()
                .filter { it.shippingAddress == "address-b" }
                .findFirst()
                .orElseThrow()
        assertThat(separate.orderCount).isEqualTo(1)
        assertThat(separate.totalPrice).isEqualTo(1250L)
        assertThat(shipments).anySatisfy {
            assertThat(it.email).isEqualTo("merge-test@example.com")
            assertThat(it.shippingAddress).isEqualTo("address-a")
            assertThat(it.zipCode).isEqualTo("99999")
            assertThat(it.orderCount).isEqualTo(1)
        }
        assertThat(shipments).anySatisfy {
            assertThat(it.email).isEqualTo("another@example.com")
            assertThat(it.shippingAddress).isEqualTo("address-a")
            assertThat(it.zipCode).isEqualTo("06234")
            assertThat(it.orderCount).isEqualTo(1)
        }
    }

    @Test
    fun missingShippingDateReturns404() {
        val exception = assertThrows(ApiServiceException::class.java) {
            service.getMergedByShippingDate(LocalDate.of(2099, 2, 3))
        }

        assertThat(exception.resultCode).isEqualTo("404-1")
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    fun findMyOrderByIdReturnsOrderWithItemsAndProductsAfterPersistenceContextClear() {
        val product = productRepository.save(Product("Single order coffee", "커피콩", 2000, "https://example.com/coffee.png"))
        val order = OrderResult(
            LocalDate.of(2098, 5, 10).atTime(11, 0),
            "single-order@example.com",
            "address-single",
            "06234"
        )
        order.addOrderItem(OrderItem(product, 2000L, 3))
        val saved = orderResultRepository.save(order)
        val savedId = requireNotNull(saved.id)

        entityManager.flush()
        entityManager.clear()

        val response = service.findMyOrderById(savedId)

        assertThat(response.id).isEqualTo(savedId)
        assertThat(response.email).isEqualTo("single-order@example.com")
        assertThat(response.shippingAddress).isEqualTo("address-single")
        assertThat(response.zipCode).isEqualTo("06234")
        assertThat(response.totalPrice).isEqualTo(6000L)
        assertThat(response.orderItemList).hasSize(1)
        val item = response.orderItemList[0]
        assertThat(item.productId).isEqualTo(product.id)
        assertThat(item.productName).isEqualTo("Single order coffee")
        assertThat(item.photoUrl).isEqualTo("https://example.com/coffee.png")
        assertThat(item.orderPrice).isEqualTo(2000L)
        assertThat(item.quantity).isEqualTo(3)
        assertThat(item.totalPrice).isEqualTo(6000L)
    }

    @Test
    fun findMyOrderByIdThrows404WhenOrderNotFound() {
        val exception = assertThrows(ApiServiceException::class.java) {
            service.findMyOrderById(999999L)
        }

        assertThat(exception.resultCode).isEqualTo("404-1")
        assertThat(exception.message).isEqualTo("해당 ID의 주문 내역은 존재하지 않습니다.")
    }

    @Test
    fun mergedShipmentsPreserveHistoricalOrderPriceAcrossMultipleOrders() {
        val shippingDate = LocalDate.of(2098, 7, 20)
        // 상품 현재가는 5000원이지만 과거 주문 당시 가격은 각각 1000원, 2000원인 경우
        val product = productRepository.save(Product("Historical price coffee", "커피콩", 5000, null))

        val first = OrderResult(
            shippingDate.atTime(9, 0),
            "historical-merge@example.com",
            "address-history",
            "06234"
        )
        first.addOrderItem(OrderItem(product, 1000L, 2)) // 2000L

        val second = OrderResult(
            shippingDate.atTime(10, 0),
            "historical-merge@example.com",
            "address-history",
            "06234"
        )
        second.addOrderItem(OrderItem(product, 2000L, 3)) // 6000L

        orderResultRepository.saveAll(listOf(first, second))

        val shipments = service.getMergedByShippingDate(shippingDate)
        val targetShipment = shipments.first { it.shippingAddress == "address-history" }

        assertThat(targetShipment.orderCount).isEqualTo(2)
        // 총금액: 1000*2 + 2000*3 = 8000 (현재가 5000*5 = 25000이 아님)
        assertThat(targetShipment.totalPrice).isEqualTo(8000L)
        assertThat(targetShipment.orderItemList).hasSize(1)
        assertThat(targetShipment.orderItemList[0].quantity).isEqualTo(5)
        assertThat(targetShipment.orderItemList[0].totalPrice).isEqualTo(8000L)
    }

    private fun order(
        shippingDate: LocalDate,
        address: String,
        product: Product,
        quantity: Int,
        email: String = "merge-test@example.com",
        zipCode: String = "06234"
    ): OrderResult {
        val order = OrderResult(shippingDate.atTime(10, 0),
                email, address, zipCode)
        order.addOrderItem(OrderItem(product, product.price.toLong(), quantity))
        return order
    }
}
