package com.programmers.be14.nbe12141team03.domain.order.entity

import com.programmers.be14.nbe12141team03.domain.product.entity.Product
import jakarta.persistence.*

@Entity
class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_RESULT_ID", nullable = false)
    var orderResult: OrderResult? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    var product: Product? = null,

    @Column(nullable = false)
    // 주문 당시 가격을 저장해 상품 가격 변경의 영향을 받지 않는다.
    var orderPrice: Long,

    @Column(nullable = false)
    var quantity: Int
) {
    constructor() : this(orderPrice = 0L, quantity = 0)

    constructor(product: Product, orderPrice: Long, quantity: Int) : this(
        id = null,
        orderResult = null,
        product = product,
        orderPrice = orderPrice,
        quantity = quantity
    )

    fun totalPrice(): Long {
        // 계산 결과가 long 범위를 초과하면 예외 발생
        return Math.multiplyExact(orderPrice, quantity.toLong())
    }
}
