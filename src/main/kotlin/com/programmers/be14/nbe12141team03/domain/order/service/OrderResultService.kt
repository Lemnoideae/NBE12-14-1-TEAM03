package com.programmers.be14.nbe12141team03.domain.order.service

import com.programmers.be14.nbe12141team03.domain.order.dto.OrderResultResponse
import com.programmers.be14.nbe12141team03.domain.order.dto.create.OrderCreateRequest
import com.programmers.be14.nbe12141team03.domain.order.dto.mergedShipment.MergedItemResponse
import com.programmers.be14.nbe12141team03.domain.order.dto.mergedShipment.MergedShipmentResponse
import com.programmers.be14.nbe12141team03.domain.order.dto.modify.OrderModifyRequest
import com.programmers.be14.nbe12141team03.domain.order.entity.OrderItem
import com.programmers.be14.nbe12141team03.domain.order.entity.OrderResult
import com.programmers.be14.nbe12141team03.domain.order.repository.OrderResultRepository
import com.programmers.be14.nbe12141team03.domain.product.repository.ProductRepository
import com.programmers.be14.nbe12141team03.global.exception.ApiServiceException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class OrderResultService(
    private val orderResultRepository: OrderResultRepository,
    private val productRepository: ProductRepository
) {
    // [관리자] 모든 고객의 모든 거래 내역 조회
    @Transactional(readOnly = true)
    fun getAllList(): List<OrderResultResponse> =
        orderResultRepository.findAllWithItemsAndProducts().map(::OrderResultResponse)

    // [관리자] 요청한 배송일에 해당하는 거래 내역 조회
    @Transactional(readOnly = true)
    fun getMergedByShippingDate(shippingDate: LocalDate): List<MergedShipmentResponse> {
        val orders = orderResultRepository.findByShippingDateWithItemsAndProducts(shippingDate)

        if (orders.isEmpty()) {
            throw ApiServiceException("404-1", "해당 배송일에 주문 내역이 없습니다.")
        }

        // 배송일에 해당하는 주문 내역을 이메일, 배송주소, 우편번호 기준으로 묶는다.
        val groups = orders.groupBy { "${it.email}|${it.shippingAddress}|${it.zipCode}" }
        return groups.values.map { toMerged(it) }
    }

    private fun toMerged(group: List<OrderResult>): MergedShipmentResponse {
        val first = group.first()
        val mergedIds = group.map { it.id }
        val totalPrice = group.fold(0L) { sum, order ->
            Math.addExact(sum, order.totalPrice)
        }

        val itemMap = LinkedHashMap<Long?, MergedItemResponse>()
        for (order in group) {
            for (item in order.orderItemList) {
                val product = item.product!!
                val productId = product.id
                itemMap.merge(
                    productId,
                    MergedItemResponse(
                        productId,
                        product.name,
                        product.photoUrl,
                        item.quantity,
                        item.totalPrice()
                    )
                ) { existing, incoming ->
                    MergedItemResponse(
                        existing.productId,
                        existing.productName,
                        existing.photoUrl,
                        existing.quantity + incoming.quantity,
                        Math.addExact(existing.totalPrice, incoming.totalPrice)
                    )
                }
            }
        }

        return MergedShipmentResponse(
            first.email,
            first.shippingDate,
            first.shippingAddress,
            first.zipCode,
            group.size,
            mergedIds,
            totalPrice,
            java.util.List.copyOf(itemMap.values)
        )
    }

    // [고객] 내 주문내역 조회
    @Transactional(readOnly = true)
    fun findMyOrders(email: String): List<OrderResultResponse> =
        orderResultRepository.findByEmailWithItemsAndProducts(email).map(::OrderResultResponse)

    // [고객] 선택한 주문내역의 ID를 통해 단건 조회
    @Transactional(readOnly = true)
    fun findMyOrderById(id: Long): OrderResultResponse = OrderResultResponse(
        orderResultRepository.findByIdWithItemsAndProducts(id).orElseThrow {
            ApiServiceException("404-1", "해당 ID의 주문 내역은 존재하지 않습니다.")
        }
    )

    // 고객 주문 생성
    @Transactional
    fun createOrder(request: OrderCreateRequest): OrderResult {
        val orderedAt = LocalDateTime.now()
        val items = request.items
        if (items.isNullOrEmpty()) {
            throw ApiServiceException("400-2", "주문 상품을 하나 이상 선택해야 합니다.")
        }

        val orderResult = OrderResult(
            orderedAt,
            request.email!!,
            request.shippingAddress!!,
            request.zipCode!!
        )

        for (item in items) {
            if (item.quantity <= 0) {
                throw ApiServiceException("400-1", "상품 수량은 1개 이상이어야 합니다.")
            }

            val product = productRepository.findById(item.productId!!).orElseThrow {
                ApiServiceException("404-1", "해당 상품을 찾을 수 없습니다.")
            }
            orderResult.addOrderItem(OrderItem(product, product.price.toLong(), item.quantity))
        }

        return orderResultRepository.save(orderResult)
    }

    // 주문 삭제
    @Transactional
    fun deleteOrder(id: Long) {
        val orderResult = orderResultRepository.findById(id).orElseThrow {
            ApiServiceException("404-1", "존재하지 않는 주문입니다.")
        }

        if (!orderResult.isModifiable(LocalDateTime.now())) {
            throw ApiServiceException("400-1", "이미 배송 준비가 시작되어 취소할 수 없습니다.")
        }

        orderResultRepository.delete(orderResult)
    }

    // 주문 수정
    @Transactional
    fun modifyOrderResult(id: Long, request: OrderModifyRequest): OrderResultResponse {
        val origin = orderResultRepository.findById(id).orElseThrow {
            ApiServiceException("404-1", "존재하지 않는 주문입니다")
        }

        if (!origin.isModifiable(LocalDateTime.now())) {
            throw ApiServiceException("400-1", "이미 배송 준비가 시작되어 수정할 수 없습니다.")
        }

        val modifiedItemList = request.orderItemList!!.map { item ->
            val product = productRepository.findById(item.productId!!).orElseThrow {
                ApiServiceException("404-2", "${item.productId}번 상품을 찾을 수 없습니다.")
            }
            OrderItem(product, product.price.toLong(), item.quantity)
        }

        origin.modify(request.shippingAddress!!, request.zipCode!!, modifiedItemList)

        // DB 업데이트를 반영해 수정 시각이 포함된 응답을 만든다.
        orderResultRepository.flush()
        return OrderResultResponse(origin)
    }
}
