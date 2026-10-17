package com.programmers.be14.nbe12141team03.domain.product.controller

import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
class ProductControllerTest {
    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    @Test
    fun `create product without photo URL keeps it null`() {
        mockMvc.post("/api/products/admin") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "name": "테스트 원두",
                  "category": "원두",
                  "price": 3000
                }
            """.trimIndent()
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.data.photoUrl", nullValue())
                jsonPath("$.data.createDate").isNotEmpty
            }
    }
}
