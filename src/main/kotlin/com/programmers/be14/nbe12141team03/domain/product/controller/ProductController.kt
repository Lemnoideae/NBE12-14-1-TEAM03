package com.programmers.be14.nbe12141team03.domain.product.controller

import com.programmers.be14.nbe12141team03.domain.product.dto.ProductCreateRequest
import com.programmers.be14.nbe12141team03.domain.product.dto.ProductResponse
import com.programmers.be14.nbe12141team03.domain.product.entity.Product
import com.programmers.be14.nbe12141team03.domain.product.service.ProductService
import com.programmers.be14.nbe12141team03.global.dto.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {
    // 상품 다건 조회
    @Tag(name = "공용")
    @Operation(
            summary = "상품 다건 조회",
            description = """
                    판매중인 상품을 전부 반환합니다.
                    """
    )

    @GetMapping
    fun getProducts() = RsData(
                "200-1",
                "상품 목록을 조회했습니다.",
                productService.getProducts()
        )

    // [관리자] 상품 추가
    @Tag(name = "관리자")
    @Operation(
            summary = "상품 추가",
            description = """
                    메뉴에 추가할 상품을 받아 저장합니다.
                    """
    )

    @PostMapping("/admin")
    fun create(@Valid @RequestBody request: ProductCreateRequest): RsData<ProductResponse> {
        val product = productService.create(
                request.name,
                request.category,
                request.price,
                request.photoUrl ?: ""
        )

        return RsData(
                "200-1",
                "${product.id}번 상품이 추가되었습니다.",
                ProductResponse(product)
        )
    }
}
