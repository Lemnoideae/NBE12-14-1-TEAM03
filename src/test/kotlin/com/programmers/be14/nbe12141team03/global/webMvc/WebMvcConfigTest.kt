package com.programmers.be14.nbe12141team03.global.webMvc

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
class WebMvcConfigTest {
    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    @Test
    @DisplayName("정적 리소스 이미지 서빙 테스트 (/images/columbia-narino.png)")
    fun serveStaticImageSuccessfully() {
        mockMvc.perform(get("/images/columbia-narino.png"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("image/png"))
            .andExpect(header().string("Cache-Control", "max-age=86400"))
    }
}
