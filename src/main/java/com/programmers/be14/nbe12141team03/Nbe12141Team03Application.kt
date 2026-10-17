package com.programmers.be14.nbe12141team03

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
open class Nbe12141Team03Application

fun main(args: Array<String>) {
    // .env 파일 로드 (환경 변수 파일이 존재하지 않더라도 오류가 나지 않도록 설정)
    val dotenv = Dotenv.configure().ignoreIfMissing().load()

    // .env의 모든 항목을 시스템 프로퍼티로 등록
    dotenv.entries().forEach { entry ->
        System.setProperty(entry.key, entry.value)
    }

    SpringApplication.run(Nbe12141Team03Application::class.java, *args)
}