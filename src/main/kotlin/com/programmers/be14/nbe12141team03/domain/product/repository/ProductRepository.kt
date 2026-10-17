package com.programmers.be14.nbe12141team03.domain.product.repository

import com.programmers.be14.nbe12141team03.domain.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository: JpaRepository<Product, Long>
