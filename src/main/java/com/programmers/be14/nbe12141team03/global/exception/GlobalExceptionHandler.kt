package com.programmers.be14.nbe12141team03.global.exception

import com.programmers.be14.nbe12141team03.global.dto.RsData
import org.springframework.context.MessageSourceResolvable
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.multipart.MaxUploadSizeExceededException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ApiServiceException::class)
    fun handleApiServiceException(e: ApiServiceException): ResponseEntity<RsData<*>> {
        val rsData = e.rsData
        return ResponseEntity.status(rsData.statusCode) // "404-1" -> 404 코드 반환
                .body(rsData)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<RsData<Map<String, String>>> {

        // 모든 필드 에러를 { "필드명": "오류 메세지" } 형태로 수집
        val errors = e.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "잘못된 요청입니다.")
        }

        // 가장 첫 번째 에러 메세지를 대표 메세지로 사용 (또는 기본 메세지)
        val defaultMsg = e.bindingResult.fieldErrors.firstOrNull()
            ?.defaultMessage
            ?: "잘못된 요청입니다."

        val rsData = RsData("400-1", defaultMsg, errors)

        return ResponseEntity.status(rsData.statusCode).body(rsData)
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleHandlerMethodValidationException(
        e: HandlerMethodValidationException): ResponseEntity<RsData<*>> {

        val defaultMsg = e.parameterValidationResults
            .flatMap { it.resolvableErrors }
            .firstOrNull()
            ?.defaultMessage
            ?: "잘못된 요청입니다."

        val rsData = RsData<Void>("400-1", defaultMsg)

        return ResponseEntity.status(rsData.statusCode).body(rsData)
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceededException(
        e: MaxUploadSizeExceededException): ResponseEntity<RsData<Void>> {

        val rsData = RsData<Void>(
            "400-1", "업로드 가능한 최대 파일 크기(10MB)를 초과했습니다.")
        return ResponseEntity.status(rsData.statusCode).body(rsData)
    }
}