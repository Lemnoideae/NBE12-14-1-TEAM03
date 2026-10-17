package com.programmers.be14.nbe12141team03.domain.image.controller

import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.nio.file.Files
import java.nio.file.Path

@SpringBootTest
class ImageControllerTest {
    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    @Test
    @DisplayName("이미지 업로드 API가 file multipart를 임시 경로에 저장")
    fun uploadImageSuccessfully() {
        val content = "fake image content".toByteArray()
        val file = MockMultipartFile("file", "test-coffee.jpg", "image/jpeg", content)

        val responseBody = mockMvc.perform(multipart("/api/images").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value("200-1"))
            .andExpect(jsonPath("$.data.photoUrl").exists())
            .andExpect(jsonPath("$.data.originalFileName").value("test-coffee.jpg"))
            .andReturn()
            .response
            .contentAsString

        val savedFileName: String = JsonPath.read(responseBody, "$.data.savedFileName")
        val photoUrl: String = JsonPath.read(responseBody, "$.data.photoUrl")
        val fileSize: Number = JsonPath.read(responseBody, "$.data.fileSize")

        assertThat(photoUrl).isEqualTo("/images/$savedFileName")
        assertThat(fileSize.toLong()).isEqualTo(content.size.toLong())
        assertThat(Files.readAllBytes(uploadDir.resolve(savedFileName))).containsExactly(*content)
    }

    companion object {
        private val uploadDir: Path = Files.createTempDirectory("team03-image-controller-test-")

        @JvmStatic
        @DynamicPropertySource
        fun registerUploadDirectory(registry: DynamicPropertyRegistry) {
            registry.add("custom.upload.image-dir") { uploadDir.toString() }
        }

        @JvmStatic
        @AfterAll
        fun cleanUpUploadDirectory() {
            uploadDir.toFile().deleteRecursively()
        }
    }
}
