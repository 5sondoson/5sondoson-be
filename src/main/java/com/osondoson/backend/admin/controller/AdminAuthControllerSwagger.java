package com.osondoson.backend.admin.controller;

import com.osondoson.backend.admin.dto.request.AdminTokenVerifyRequest;
import com.osondoson.backend.common.config.ApiErrorExamples;
import com.osondoson.backend.common.response.APIErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "어드민 - 인증", description = "어드민 토큰 검증 API")
public interface AdminAuthControllerSwagger {

    @Operation(
            summary = "어드민 토큰 검증",
            description = "어드민 페이지 진입 시 토큰 유효성을 검증한다. 유효하면 200, 유효하지 않으면 403을 반환한다. "
                    + "이후 어드민 API 호출 시에는 검증된 토큰을 X-ADMIN-TOKEN 헤더에 담아 보낸다."
    )
    @ApiResponse(responseCode = "200", description = "토큰 유효 (응답 본문 없음)")
    @ApiResponse(
            responseCode = "403",
            description = "토큰이 유효하지 않음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = APIErrorResponse.class),
                    examples = @ExampleObject(name = "FORBIDDEN_ADMIN_TOKEN", value = ApiErrorExamples.FORBIDDEN_ADMIN_TOKEN)
            )
    )
    ResponseEntity<Void> verifyToken(AdminTokenVerifyRequest adminTokenVerifyRequest);
}
