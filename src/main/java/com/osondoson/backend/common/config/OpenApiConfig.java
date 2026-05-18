package com.osondoson.backend.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String ADMIN_TOKEN_SCHEME = "AdminToken";
    private static final String ADMIN_TOKEN_HEADER = "X-ADMIN-TOKEN";

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
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes(ADMIN_TOKEN_SCHEME, adminTokenSecurityScheme()));
    }

    private SecurityScheme adminTokenSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(ADMIN_TOKEN_HEADER)
                .description("어드민 토큰 검증 API로 발급/검증한 토큰을 입력한다.");
    }
}
