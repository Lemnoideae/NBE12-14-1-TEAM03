package com.programmers.be14.nbe12141team03.domain.product.dto

import com.programmers.be14.nbe12141team03.domain.product.entity.Product
import java.time.LocalDateTime

data class ProductResponse(
    val id: Long?,
    val name: String,
    val category: String,
    val price: Int,
    val photoUrl: String?,
    val createDate: LocalDateTime?,
    val modifyDate: LocalDateTime?
) {

    constructor(product: Product) : this(
        id = product.id,
        name = product.name,
        category = product.category,
        price = product.price,
        photoUrl = product.photoUrl,
        createDate = product.createDate,
        modifyDate = product.modifyDate
    )
}
