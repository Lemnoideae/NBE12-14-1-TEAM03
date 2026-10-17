package com.programmers.be14.nbe12141team03.domain.image.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "이미지 업로드 응답 DTO")
data class ImageUploadResponse(

    @Schema(description = "이미지 접근 URL 경로", example = "/images/a1b2c3d4_columbia.png")
    val photoUrl: String,

    @Schema(description = "원본 파일명", example = "columbia.png")
    val originalFileName: String,

    @Schema(description = "저장된 파일명", example = "a1b2c3d4_columbia.png")
    val savedFileName: String,

    @Schema(description = "파일 크기 (Byte)", example = "1048576")
    val fileSize: Long

) {}