package com.programmers.be14.nbe12141team03.domain.product.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class ProductCreateRequest(

    @NotBlank(message = "상품 이름을 입력해주세요.")
    val name: String,

    @NotBlank(message = "카테고리를 입력해주세요.")
    val category: String,

    @Positive(message = "상품 가격은 음수이거나 0일 수 없습니다.")
    val price: Int,

    val photoUrl: String?
)
