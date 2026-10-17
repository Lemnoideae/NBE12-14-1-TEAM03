package com.programmers.be14.nbe12141team03.domain.order.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@EntityListeners(AuditingEntityListener::class)
open class OrderResult() {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @field:Column(nullable = false)
    open var createDate: LocalDateTime? = null
        protected set

    @field:LastModifiedDate
    open var modifyDate: LocalDateTime? = null
        protected set

    @field:Column(nullable = false)
    open var email: String? = null
        protected set

    @field:Column(nullable = false)
    open var shippingAddress: String? = null
        protected set

    @field:Column(nullable = false)
    open var zipCode: String? = null
        protected set

    open var totalPrice: Long = 0L
        protected set

    @field:Column(nullable = false)
    open var shippingDate: LocalDate? = null
        protected set

    @field:OneToMany(mappedBy = "orderResult", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var orderItemList: MutableList<OrderItem> = mutableListOf()
        protected set

    constructor(
        createDate: LocalDateTime,
        email: String,
        shippingAddress: String,
        zipCode: String
    ) : this() {
        this.createDate = createDate
        this.email = email
        this.shippingAddress = shippingAddress
        this.zipCode = zipCode
        this.shippingDate = calculateShipping(createDate)
    }

    // 연관관계 편의 메서드
    fun addOrderItem(orderItem: OrderItem) {
        orderItemList.add(orderItem)
        orderItem.orderResult = this
        // 총금액이 long 범위 초과하면 예외 발생
        totalPrice = Math.addExact(totalPrice, orderItem.totalPrice())
    }

    // 14:00 미만의 주문은 당일 배송
    private fun calculateShipping(createDate: LocalDateTime): LocalDate {
        val currentTime = createDate.toLocalTime()
        val baseTime = LocalTime.of(14, 0)
        return if (currentTime.isBefore(baseTime)) createDate.toLocalDate()
        else createDate.toLocalDate().plusDays(1)
    }

    // 배송일 14:00부터는 주문 수정 불가
    fun isModifiable(now: LocalDateTime): Boolean =
        now.isBefore(requireNotNull(shippingDate).atTime(14, 0))

    // 주문 내역 수정
    fun modify(shippingAddress: String, zipCode: String, newItemList: List<OrderItem>) {
        this.shippingAddress = shippingAddress
        this.zipCode = zipCode

        // 기존 주문 상품을 고아로 만들어서 DB에서 삭제
        orderItemList.clear()
        totalPrice = 0L

        newItemList.forEach(::addOrderItem)
    }
}
