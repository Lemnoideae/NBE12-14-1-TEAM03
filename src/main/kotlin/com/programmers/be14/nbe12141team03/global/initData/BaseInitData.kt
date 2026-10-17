package com.programmers.be14.nbe12141team03.global.initData

import com.programmers.be14.nbe12141team03.domain.order.entity.OrderItem
import com.programmers.be14.nbe12141team03.domain.order.entity.OrderResult
import com.programmers.be14.nbe12141team03.domain.order.repository.OrderResultRepository
import com.programmers.be14.nbe12141team03.domain.product.entity.Product
import com.programmers.be14.nbe12141team03.domain.product.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Configuration
class BaseInitData(
    private val orderResultRepository: OrderResultRepository,
    private val productRepository: ProductRepository
) {
    @field:Autowired
    @field:Lazy
    private lateinit var self: BaseInitData

    @Bean
    fun initDataRunner(): ApplicationRunner = ApplicationRunner {
        self.work1()
        self.work2()
    }

    @Transactional
    fun work1() {
        if (productRepository.count() > 0) return

        productRepository.saveAll(listOf(
            Product("Columbia Nariñó", "커피콩", 5000, "/images/columbia-narino.png"),
            Product("Brazil Serra Do Caparaó", "커피콩", 6000, "/images/brazil-serra.png"),
            Product("Columbia Quindío", "커피콩", 8000, "/images/columbia-quindio.png"),
            Product("Ethiopia Sidamo", "커피콩", 5500, "/images/ethiopia-sidamo.png")
        ))
    }

    @Transactional
    fun work2() {
        if (orderResultRepository.count() > 0) return
        val products = productRepository.findAll()

        val narino = products[0]      // 5000원
        val brazil = products[1]      // 6000원
        val quindio = products[2]     // 8000원
        val ethiopia = products[3]    // 5500원

        val today = LocalDate.now()

        // ===== 지난 배송 (수정/취소 불가 케이스) =====

        // 3일 전 배송 - 합배송 2건
        val p1 = OrderResult(
            orderedBefore(today.minusDays(3), 10, 15),
            "hoonhee@test.com", "서울시 강남구 테헤란로 1", "06234"
        ).apply {
            addOrderItem(OrderItem(narino, narino.price.toLong(), 1))
        }

        val p2 = OrderResult(
            orderedAfter(today.minusDays(3), 16, 40),
            "hoonhee@test.com", "서울시 강남구 테헤란로 1", "06234"
        ).apply {
            addOrderItem(OrderItem(quindio, quindio.price.toLong(), 2))
        }

        // 2일 전 배송
        val p3 = OrderResult(
            orderedAfter(today.minusDays(2), 18, 5),
            "minji@test.com", "광주시 서구 상무대로 7", "61949"
        ).apply {
            addOrderItem(OrderItem(brazil, brazil.price.toLong(), 3))
            addOrderItem(OrderItem(ethiopia, ethiopia.price.toLong(), 1))
        }

        // 어제 배송 - 같은 이메일이지만 배송지가 달라 분리되는 케이스
        val p4 = OrderResult(
            orderedBefore(today.minusDays(1), 9, 30),
            "sujee@test.com", "부산시 해운대구 우동 2", "48095"
        ).apply {
            addOrderItem(OrderItem(narino, narino.price.toLong(), 2))
        }

        val p5 = OrderResult(
            orderedAfter(today.minusDays(1), 20, 12),
            "sujee@test.com", "부산시 수영구 광안로 9", "48300"
        ).apply {
            addOrderItem(OrderItem(ethiopia, ethiopia.price.toLong(), 1))
        }

        // ===== 오늘 배송 =====

        // 합배송 3건 - 같은 상품이 여러 주문에 걸쳐 합산되는 케이스
        val t1 = OrderResult(
            orderedAfter(today, 14, 0),          // 어제 14:00 정각 - 경계값
            "hoonhee@test.com", "서울시 강남구 테헤란로 1", "06234"
        ).apply {
            addOrderItem(OrderItem(narino, narino.price.toLong(), 2))
            addOrderItem(OrderItem(brazil, brazil.price.toLong(), 1))
        }

        val t2 = OrderResult(
            orderedAfter(today, 22, 45),
            "hoonhee@test.com", "서울시 강남구 테헤란로 1", "06234"
        ).apply {
            addOrderItem(OrderItem(narino, narino.price.toLong(), 3))
        }

        val t3 = OrderResult(
            orderedBefore(today, 13, 59),        // 오늘 13:59 - 경계값
            "hoonhee@test.com", "서울시 강남구 테헤란로 1", "06234"
        ).apply {
            addOrderItem(OrderItem(quindio, quindio.price.toLong(), 1))
            addOrderItem(OrderItem(ethiopia, ethiopia.price.toLong(), 2))
        }

        // 단독 주문
        val t4 = OrderResult(
            orderedBefore(today, 8, 20),
            "sujee@test.com", "부산시 해운대구 우동 2", "48095"
        ).apply {
            addOrderItem(OrderItem(ethiopia, ethiopia.price.toLong(), 1))
        }

        // 상품 4종 전부
        val t5 = OrderResult(
            orderedAfter(today, 15, 33),
            "jaechul@test.com", "인천시 연수구 송도과학로 4", "21999"
        ).apply {
            addOrderItem(OrderItem(narino, narino.price.toLong(), 1))
            addOrderItem(OrderItem(brazil, brazil.price.toLong(), 1))
            addOrderItem(OrderItem(quindio, quindio.price.toLong(), 1))
            addOrderItem(OrderItem(ethiopia, ethiopia.price.toLong(), 1))
        }

        // 대량 주문
        val t6 = OrderResult(
            orderedAfter(today, 19, 8),
            "cafe@test.com", "서울시 마포구 홍익로 12", "04039"
        ).apply {
            addOrderItem(OrderItem(narino, narino.price.toLong(), 20))
            addOrderItem(OrderItem(brazil, brazil.price.toLong(), 15))
        }

        // ===== 내일 배송 =====

        val n1 = OrderResult(
            orderedAfter(today.plusDays(1), 14, 1),
            "heewon@test.com", "대전시 유성구 대학로 3", "34126"
        ).apply {
            addOrderItem(OrderItem(brazil, brazil.price.toLong(), 2))
        }


        val n2 = OrderResult(
            orderedAfter(today.plusDays(1), 17, 22),
            "heewon@test.com", "대전시 유성구 대학로 3", "34126"
        ).apply {
            addOrderItem(OrderItem(quindio, quindio.price.toLong(), 1))
        }

        val n3 = OrderResult(
            orderedAfter(today.plusDays(1), 21, 50),
            "dukwoo@test.com", "제주시 첨단로 8", "63309"
        ).apply {
            addOrderItem(OrderItem(narino, narino.price.toLong(), 5))
        }

        // ===== 모레 이후 =====

        val f1 = OrderResult(
            orderedAfter(today.plusDays(2), 16, 15),
            "jaechul@test.com", "인천시 연수구 송도과학로 4", "21999"
        ).apply {
            addOrderItem(OrderItem(quindio, quindio.price.toLong(), 1))
        }

        val f2 = OrderResult(
            orderedAfter(today.plusDays(2), 23, 3),
            "minji@test.com", "광주시 서구 상무대로 7", "61949"
        ).apply {
            addOrderItem(OrderItem(ethiopia, ethiopia.price.toLong(), 4))
            addOrderItem(OrderItem(narino, narino.price.toLong(), 2))
        }

        val f3 = OrderResult(
            orderedAfter(today.plusDays(3), 14, 30),
            "cafe@test.com", "서울시 마포구 홍익로 12", "04039"
        ).apply {
            addOrderItem(OrderItem(brazil, brazil.price.toLong(), 10))
        }

        // ===== 대량 합배송 (오늘, 12건) =====
        // 한 고객이 하루에 여러 번 주문한 케이스 - 배지 목록 UI 확인용
        val bulkToday = ArrayList<OrderResult>()
        for (i in 1..12) {
            val b = OrderResult(
                orderedAfter(today, 14 + (i % 9), i * 4 % 60),
                "bulk@test.com", "서울시 종로구 세종대로 100", "03172"
            )

            val p = products[i % 4]
            b.addOrderItem(OrderItem(p, p.price.toLong(), (i % 3) + 1))

            bulkToday.add(b)
        }

        // ===== 초대량 합배송 (내일, 25건) =====
        val bulkTomorrow = ArrayList<OrderResult>()
        for (i in 1..25) {
            val b = OrderResult(
                orderedAfter(today.plusDays(1), 14 + (i % 9), i * 7 % 60),
                "heavy@test.com", "경기도 성남시 분당구 판교로 200", "13529"
            )

            b.addOrderItem(OrderItem(narino, narino.price.toLong(), 1))
            if (i % 2 == 0) {
                b.addOrderItem(OrderItem(brazil, brazil.price.toLong(), 2))
            }

            bulkTomorrow.add(b)
        }

        orderResultRepository.apply {
            saveAll(listOf(
                p1, p2, p3, p4, p5,
                t1, t2, t3, t4, t5, t6,
                n1, n2, n3,
                f1, f2, f3
                ))
            saveAll(bulkToday)
            saveAll(bulkTomorrow)
        }
    }

    // 14:00 이전 주문 - 배송일 당일에 주문한 것으로 처리
    private fun orderedBefore(shippingDate: LocalDate, hour: Int, minute: Int): LocalDateTime {
        if (hour >= 14) throw IllegalArgumentException("14시 미만이어야 합니다")
        return shippingDate.atTime(hour, minute)
    }

    // 14:00 정각부터의 주문 - 배송일 하루 전에 주문한 것으로 처리
    private fun orderedAfter(shippingDate: LocalDate, hour: Int, minute: Int): LocalDateTime {
        if (hour < 14) throw IllegalArgumentException("14시 이후이어야 합니다")
        return shippingDate.minusDays(1).atTime(hour, minute)
    }
}
