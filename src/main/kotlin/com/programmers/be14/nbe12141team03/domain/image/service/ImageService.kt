package com.programmers.be14.nbe12141team03.domain.image.service

import com.programmers.be14.nbe12141team03.domain.image.dto.ImageUploadResponse
import com.programmers.be14.nbe12141team03.global.exception.ApiServiceException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

@Service
class ImageService {

    private companion object {
        private val log = LoggerFactory.getLogger(ImageService::class.java)
        private val ALLOWED_EXTENSIONS: Set<String> = setOf("png", "jpg", "jpeg", "webp")
        private val ALLOWED_MIME_TYPES: Set<String> = setOf(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/webp"
        )
    }

    @Value($$"${custom.upload.image-dir:images/}")
    private lateinit var imageDir: String

    fun uploadImage(file: MultipartFile?): ImageUploadResponse {
        val uploadFile = validateFile(file)

        val originalFilename = uploadFile.originalFilename
        if (originalFilename == null || '.' !in originalFilename) {
            throw ApiServiceException("400-2", "올바르지 않은 파일명입니다.")
        }

        val extension = extractExtension(originalFilename)

        if (extension !in ALLOWED_EXTENSIONS) {
            throw ApiServiceException(
                "400-3",
                "지원하지 않는 이미지 확장자입니다. (지원 형식: PNG, JPG, JPEG, WebP)"
            )
        }

        val cleanFilename = Paths.get(originalFilename).fileName.toString()
            .replace("[^a-zA-Z0-9._-]".toRegex(), "_")

        val savedFileName = "${UUID.randomUUID()}_$cleanFilename"

        val uploadPath = Paths.get(imageDir).toAbsolutePath()

        try {
            Files.createDirectories(uploadPath)

            val targetLocation = uploadPath.resolve(savedFileName)
            uploadFile.inputStream.use { inputStream ->
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
            }
        } catch (e: IOException) {
            log.error("이미지 파일 저장 실패: {}", e.message, e)
            throw ApiServiceException(
                "500-1", "이미지 파일 저장 중 오류가 발생했습니다."
            )
        }

        val photoUrl = "/images/$savedFileName"

        return ImageUploadResponse(
            photoUrl,
            originalFilename,
            savedFileName,
            uploadFile.size
        )
    }

    private fun validateFile(file: MultipartFile?): MultipartFile {
        if (file == null || file.isEmpty) {
            throw ApiServiceException("400-1", "업로드할 이미지 파일이 비어있습니다.")
        }

        val contentType = file.contentType
        if (contentType != null && contentType.lowercase() !in ALLOWED_MIME_TYPES) {
            throw ApiServiceException("400-4", "지원하지 않는 이미지 MIME 타입입니다.")
        }
        return file
    }

    private fun extractExtension(filename: String): String {
        val lastDotIndex = filename.lastIndexOf(".")
        return if (lastDotIndex == -1) "" else filename.substring(lastDotIndex + 1).lowercase()
    }
}
