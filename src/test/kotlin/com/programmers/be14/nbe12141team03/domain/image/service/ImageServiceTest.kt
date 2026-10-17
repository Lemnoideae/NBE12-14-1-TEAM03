package com.programmers.be14.nbe12141team03.domain.image.service

import com.programmers.be14.nbe12141team03.global.exception.ApiServiceException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.util.ReflectionTestUtils
import java.nio.file.Files
import java.nio.file.Path

class ImageServiceTest {
    @TempDir
    private lateinit var tempDir: Path

    @Test
    @DisplayName("PNG 이미지 업로드 성공")
    fun uploadPngImageSuccessfully() {
        val content = "dummy image content".toByteArray()
        val file = MockMultipartFile("file", "test-image.png", "image/png", content)

        val response = createServiceWithTempDir().uploadImage(file)

        assertThat(response.originalFileName).isEqualTo("test-image.png")
        assertThat(response.photoUrl).startsWith("/images/").endsWith(".png")
        assertThat(Files.readAllBytes(tempDir.resolve(response.savedFileName)))
            .containsExactly(*content)
    }

    @Test
    @DisplayName("WebP 이미지 업로드 성공")
    fun uploadWebpImageSuccessfully() {
        val content = "dummy webp content".toByteArray()
        val file = MockMultipartFile("file", "coffee.webp", "image/webp", content)

        val response = createServiceWithTempDir().uploadImage(file)

        assertThat(response.originalFileName).isEqualTo("coffee.webp")
        assertThat(response.photoUrl).startsWith("/images/").endsWith(".webp")
        assertThat(Files.readAllBytes(tempDir.resolve(response.savedFileName)))
            .containsExactly(*content)
    }

    @Test
    @DisplayName("빈 파일 업로드 시 400-1 오류")
    fun uploadEmptyFileIsRejected() {
        val file = MockMultipartFile("file", "empty.png", "image/png", byteArrayOf())

        assertUploadRejected(file, "400-1")
    }

    @Test
    @DisplayName("null 파일 업로드 시 400-1 오류")
    fun uploadNullFileIsRejected() {
        assertUploadRejected(null, "400-1")
    }

    @Test
    @DisplayName("확장자 구분자가 없는 파일명은 400-2 오류")
    fun uploadFilenameWithoutExtensionIsRejected() {
        val file = MockMultipartFile("file", "filename", "image/png", "content".toByteArray())

        assertUploadRejected(file, "400-2")
    }

    @Test
    @DisplayName("지원하지 않는 확장자는 400-3 오류")
    fun uploadUnsupportedExtensionIsRejected() {
        val file = MockMultipartFile("file", "document.txt", "image/png", "content".toByteArray())

        assertUploadRejected(file, "400-3")
    }

    @Test
    @DisplayName("지원하지 않는 MIME 타입은 400-4 오류")
    fun uploadUnsupportedMimeTypeIsRejected() {
        val file = MockMultipartFile("file", "document.png", "text/plain", "content".toByteArray())

        assertUploadRejected(file, "400-4")
    }

    private fun assertUploadRejected(file: MockMultipartFile?, resultCode: String) {
        assertThatThrownBy { createServiceWithTempDir().uploadImage(file) }
            .isInstanceOfSatisfying(ApiServiceException::class.java) { exception ->
                assertThat(exception.rsData.resultCode).isEqualTo(resultCode)
            }
    }

    private fun createServiceWithTempDir(): ImageService = ImageService().also {
        ReflectionTestUtils.setField(it, "imageDir", tempDir.toString())
    }
}
