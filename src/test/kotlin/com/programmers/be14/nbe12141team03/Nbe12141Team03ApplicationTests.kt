package com.programmers.be14.nbe12141team03

import com.programmers.be14.nbe12141team03.domain.order.repository.OrderResultRepository
import com.programmers.be14.nbe12141team03.domain.product.repository.ProductRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.DefaultApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class Nbe12141Team03ApplicationTests {
    @Autowired
    private lateinit var applicationRunner: ApplicationRunner

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var orderResultRepository: OrderResultRepository

    @Test
    fun contextLoads() {}

    @Test
    fun initialDataRunnerDoesNotCreateDuplicateProductsOrOrders() {
        val productCount = productRepository.count()
        val orderCount = orderResultRepository.count()

        assertThat(productRepository.findAll().map { it.name })
            .contains("Columbia Nariñó", "Brazil Serra Do Caparaó", "Columbia Quindío", "Ethiopia Sidamo")
        assertThat(orderResultRepository.findByEmailWithItemsAndProducts("hoonhee@test.com"))
            .isNotEmpty()
        assertThat(
            orderResultRepository.findByShippingDateWithItemsAndProducts(LocalDate.now())
                .map { it.email }
        ).contains("hoonhee@test.com")

        applicationRunner.run(DefaultApplicationArguments())

        assertEquals(productCount, productRepository.count())
        assertEquals(orderCount, orderResultRepository.count())
    }
}
