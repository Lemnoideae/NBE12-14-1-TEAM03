package com.programmers.be14.nbe12141team03.global.webMvc

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Paths
import java.time.Duration

@Configuration
class WebMvcConfig(
    @Value("\${custom.upload.image-dir:images/}") private val imageDir: String
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)

        registry.addMapping("/images/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "OPTIONS")
            .allowedHeaders("*")
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val location = Paths.get(imageDir).toAbsolutePath().toUri().toString()
        val normalizedLocation = if (location.endsWith("/")) location else "$location/"

        registry.addResourceHandler("/images/**")
            .addResourceLocations(normalizedLocation)
            .setCacheControl(CacheControl.maxAge(Duration.ofDays(1)))
    }
}
