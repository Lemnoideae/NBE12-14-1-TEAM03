package com.programmers.be14.nbe12141team03.domain.product.service

import com.programmers.be14.nbe12141team03.domain.product.dto.ProductResponse
import com.programmers.be14.nbe12141team03.domain.product.entity.Product
import com.programmers.be14.nbe12141team03.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    fun getProducts(): List<ProductResponse> = productRepository.findAll()
        .map { ProductResponse(it) }

    fun create(
        name: String,
        category: String,
        price: Int,
        photoUrl: String?
    ) = Product(name, category, price, photoUrl).also {
        productRepository.save(it)
    }
}
