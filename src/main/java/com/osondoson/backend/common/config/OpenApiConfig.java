package com.osondoson.backend.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI osondosonOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Osondoson API")
                        .description("""
                                선수 이적 예측 서비스 API 문서

                                ### 리그 코드
                                - **5대리그**: EPL, LA, BL, SA, L1
                                - **비5대리그**: ERE, PRL, BPL
                                """)
                        .version("v1"));
    }
}
