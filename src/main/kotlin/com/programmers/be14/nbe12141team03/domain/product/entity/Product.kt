package com.programmers.be14.nbe12141team03.domain.product.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class Product(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var category: String,

    @Column(nullable = false)
    var price: Int,

    @Column
    var photoUrl: String? = null,

    @CreatedDate
    var createDate: LocalDateTime? = null,

    @LastModifiedDate
    var modifyDate: LocalDateTime? = null

) {
    protected constructor() : this(name = "", category = "", price = 0)

    constructor(name: String, category: String, price: Int, photoUrl: String?) : this(
        id = null,
        name = name,
        category = category,
        price = price,
        photoUrl = photoUrl
    )
}
