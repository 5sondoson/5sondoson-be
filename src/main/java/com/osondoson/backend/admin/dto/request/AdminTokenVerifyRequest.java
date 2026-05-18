package com.osondoson.backend.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record AdminTokenVerifyRequest(
        @Schema(description = "검증할 어드민 토큰", example = "1234")
        String adminToken
) {
}
