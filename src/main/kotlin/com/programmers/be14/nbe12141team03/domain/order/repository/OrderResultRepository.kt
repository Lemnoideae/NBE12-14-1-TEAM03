package com.programmers.be14.nbe12141team03.domain.order.repository

import com.programmers.be14.nbe12141team03.domain.order.entity.OrderResult
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.util.Optional

interface OrderResultRepository : JpaRepository<OrderResult, Long> {
    @Query(
        """
        select distinct o
        from OrderResult o
        join fetch o.orderItemList oi
        join fetch oi.product
        where o.id = :id
        """
    )
    fun findByIdWithItemsAndProducts(@Param("id") id: Long): Optional<OrderResult>

    @Query(
        """
        select distinct o
        from OrderResult o
        join fetch o.orderItemList oi
        join fetch oi.product
        order by o.createDate desc
        """
    )
    fun findAllWithItemsAndProducts(): List<OrderResult>

    @Query(
        """
        select distinct o
        from OrderResult o
        join fetch o.orderItemList oi
        join fetch oi.product
        where o.email = :email
        order by o.createDate desc
        """
    )
    fun findByEmailWithItemsAndProducts(@Param("email") email: String): List<OrderResult>

    @Query(
        """
        select distinct o
        from OrderResult o
        join fetch o.orderItemList oi
        join fetch oi.product
        where o.shippingDate = :shippingDate
        """
    )
    fun findByShippingDateWithItemsAndProducts(
        @Param("shippingDate") shippingDate: LocalDate
    ): List<OrderResult>
}
