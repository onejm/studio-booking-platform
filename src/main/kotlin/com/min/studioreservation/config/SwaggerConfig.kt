package com.min.studioreservation.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Studio Reservation API")
                    .description("스튜디오 예약 플랫폼 API 문서")
                    .version("v1")
                    .contact(
                        Contact()
                            .name("studio-reservation")
                    )
            )
}
