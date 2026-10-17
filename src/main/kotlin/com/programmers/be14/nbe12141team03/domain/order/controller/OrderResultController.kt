package com.programmers.be14.nbe12141team03.domain.order.controller

import com.programmers.be14.nbe12141team03.domain.order.dto.OrderResultResponse
import com.programmers.be14.nbe12141team03.domain.order.dto.create.OrderCreateRequest
import com.programmers.be14.nbe12141team03.domain.order.dto.create.OrderCreateResponse
import com.programmers.be14.nbe12141team03.domain.order.dto.mergedShipment.MergedShipmentResponse
import com.programmers.be14.nbe12141team03.domain.order.dto.modify.OrderModifyRequest
import com.programmers.be14.nbe12141team03.domain.order.service.OrderResultService
import com.programmers.be14.nbe12141team03.global.dto.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/orders")
class OrderResultController(
    private val orderResultService: OrderResultService
) {
    // [관리자] 다건 조회
    @Operation(summary = "전체 주문 내역 다건 조회")
    @Tag(name = "관리자")
    @GetMapping("/admin")
    fun adminOrderResultList(): RsData<List<OrderResultResponse>> = RsData(
        "200-1",
        "모든 고객의 전체 주문 내역을 조회했습니다.",
        orderResultService.allList
    )

    // [관리자] 배송일 기준 합배송 내역 조회
    @Operation(summary = "합배송 다건 조회")
    @Tag(name = "관리자")
    @GetMapping("/admin/shipments")
    fun adminShipmentList(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") shippingDate: LocalDate
    ): RsData<List<MergedShipmentResponse>> = RsData(
        "200-1",
        "해당 배송일의 합배송 내역을 조회했습니다.",
        orderResultService.getMergedByShippingDate(shippingDate)
    )

    // 주문 내역 수정
    @Operation(summary = "주문 수정")
    @Tag(name = "공용")
    @PatchMapping("/{id}")
    fun modifyOrderResult(
        @PathVariable id: Long,
        @Valid @RequestBody request: OrderModifyRequest
    ): RsData<OrderResultResponse> = RsData(
        "200-1",
        "${id}번 주문이 수정되었습니다.",
        orderResultService.modifyOrderResult(id, request)
    )

    // [고객] 다건 조회
    @Operation(summary = "자신의 주문 내역 다건 조회")
    @Tag(name = "공용")
    @GetMapping("/mypage")
    fun getMyOrders(
        @RequestParam
        @NotBlank(message = "Email을 입력해 주세요.")
        @Email(message = "올바른 Email의 형식이 아닙니다.")
        email: String
    ): RsData<List<OrderResultResponse>> {
        val orders = orderResultService.findMyOrders(email)
        val message = if (orders.isEmpty()) "주문 내역이 없습니다."
        else "현재 고객의 전체 주문 내역을 조회했습니다."

        return RsData("200-1", message, orders)
    }

    // [고객] 단건 조회
    @Operation(summary = "주문 내역 단건 조회")
    @Tag(name = "공용")
    @GetMapping("/mypage/{id}")
    fun getMyOrderById(@PathVariable id: Long): RsData<OrderResultResponse> = RsData(
        "200-1",
        "현재 고객의 주문 내역 중 선택한 ID의 내역을 조회했습니다.",
        orderResultService.findMyOrderById(id)
    )

    // 주문 생성
    @Operation(summary = "주문 생성")
    @Tag(name = "공용")
    @PostMapping
    fun createOrder(@Valid @RequestBody request: OrderCreateRequest): RsData<OrderCreateResponse> {
        val orderResult = orderResultService.createOrder(request)
        return RsData(
            "200-1",
            "${orderResult.id}번 주문이 생성되었습니다.",
            OrderCreateResponse(orderResult)
        )
    }

    // 주문 삭제
    @Operation(summary = "주문 삭제")
    @Tag(name = "공용")
    @DeleteMapping("/{id}")
    fun deleteOrder(@PathVariable id: Long): RsData<Void> {
        orderResultService.deleteOrder(id)
        return RsData("200-1", "${id}번 주문이 삭제되었습니다.")
    }
}
