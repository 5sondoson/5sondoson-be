package com.osondoson.backend.admin.controller;

import com.osondoson.backend.admin.dto.request.PredictionBatchRequest;
import com.osondoson.backend.common.config.ApiErrorExamples;
import com.osondoson.backend.common.response.APIErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "어드민 - 예측 배치", description = "AI 예측 데이터 배치 적재 API")
public interface AdminPredictionControllerSwagger {

    @Operation(
            summary = "전체 예측 파이프라인 실행",
            description = "성과 예측 → 시장가치 예측 → 유사선수 매칭을 순서대로 적재한다. "
                    + "요청 즉시 202를 반환하고 실제 적재는 비동기로 수행된다. 단계 실패 시 즉시 중단(FAIL_FAST)."
    )
    @ApiResponse(responseCode = "202", description = "배치 작업 접수 (응답 본문 없음)")
    @ApiResponse(
            responseCode = "400",
            description = "리그 값이 올바르지 않음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = APIErrorResponse.class),
                    examples = @ExampleObject(name = "INVALID_LEAGUE", value = ApiErrorExamples.INVALID_LEAGUE)
            )
    )
    ResponseEntity<Void> triggerPipeline(PredictionBatchRequest predictionBatchRequest);

    @Operation(
            summary = "성과 예측 적재",
            description = "전체 활성 선수를 50명 단위 청크로 나누어 AI 성과 예측을 적재한다. 기존 데이터가 있으면 갱신(upsert)한다."
    )
    @ApiResponse(responseCode = "202", description = "배치 작업 접수 (응답 본문 없음)")
    @ApiResponse(
            responseCode = "400",
            description = "리그 값이 올바르지 않음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = APIErrorResponse.class),
                    examples = @ExampleObject(name = "INVALID_LEAGUE", value = ApiErrorExamples.INVALID_LEAGUE)
            )
    )
    ResponseEntity<Void> triggerPerformance(PredictionBatchRequest predictionBatchRequest);

    @Operation(
            summary = "시장가치 예측 적재",
            description = "전체 활성 선수의 시장가치 예측을 적재한다. 성과 예측이 먼저 적재된 선수만 처리되며, 없는 선수는 건너뛴다."
    )
    @ApiResponse(responseCode = "202", description = "배치 작업 접수 (응답 본문 없음)")
    @ApiResponse(
            responseCode = "400",
            description = "리그 값이 올바르지 않음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = APIErrorResponse.class),
                    examples = @ExampleObject(name = "INVALID_LEAGUE", value = ApiErrorExamples.INVALID_LEAGUE)
            )
    )
    ResponseEntity<Void> triggerMarketValue(PredictionBatchRequest predictionBatchRequest);

    @Operation(
            summary = "유사선수 매칭 적재",
            description = "전체 활성 선수의 유사선수 매칭을 적재한다. 성과 예측이 선행되어야 하며, 기존 유사선수 데이터는 삭제 후 재적재(replace)한다."
    )
    @ApiResponse(responseCode = "202", description = "배치 작업 접수 (응답 본문 없음)")
    @ApiResponse(
            responseCode = "400",
            description = "리그 값이 올바르지 않음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = APIErrorResponse.class),
                    examples = @ExampleObject(name = "INVALID_LEAGUE", value = ApiErrorExamples.INVALID_LEAGUE)
            )
    )
    ResponseEntity<Void> triggerSimilarPlayers(PredictionBatchRequest predictionBatchRequest);
}
