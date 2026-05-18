package com.osondoson.backend.domain.player.controller;

import com.osondoson.backend.common.config.ApiErrorExamples;
import com.osondoson.backend.common.response.APIErrorResponse;
import com.osondoson.backend.common.response.APISuccessResponse;
import com.osondoson.backend.domain.player.dto.request.FeaturedPlayersRequest;
import com.osondoson.backend.domain.player.dto.request.PlayerSearchRequest;
import com.osondoson.backend.domain.player.dto.response.FeaturedPlayersResponse;
import com.osondoson.backend.domain.player.dto.response.PlayerHistoryResponse;
import com.osondoson.backend.domain.player.dto.response.PlayerPredictResponse;
import com.osondoson.backend.domain.player.dto.response.PlayerProfileResponse;
import com.osondoson.backend.domain.player.dto.response.PlayerSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(name = "선수", description = "선수 조회·검색·이적 예측 API")
public interface PlayerControllerSwagger {

    @Operation(
            summary = "선수 검색",
            description = "선수명 키워드로 비5대리그(에레디비지 / 프리메이라 리가 / 벨기에 프로 리그) 소속 선수를 검색한다. "
                    + "리그·포지션 필터와 페이지네이션을 지원하며, 결과가 없으면 빈 배열을 반환한다."
    )
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @ApiResponse(
            responseCode = "400",
            description = "리그 또는 포지션 값이 올바르지 않음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = APIErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "INVALID_LEAGUE", value = ApiErrorExamples.INVALID_LEAGUE),
                            @ExampleObject(name = "INVALID_POSITION", value = ApiErrorExamples.INVALID_POSITION)
                    }
            )
    )
    ResponseEntity<APISuccessResponse<PlayerSearchResponse>> search(
            @ParameterObject PlayerSearchRequest playerSearchRequest
    );

    @Operation(
            summary = "선수 프로필 조회",
            description = "선수 상세 페이지 상단에 고정되는 프로필 헤더 데이터(이름·포지션·클럽·국적·현재 시장가치·계약 만료일 등)를 반환한다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(
            responseCode = "404",
            description = "해당 선수를 찾을 수 없음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = APIErrorResponse.class),
                    examples = @ExampleObject(name = "PLAYER_NOT_FOUND", value = ApiErrorExamples.PLAYER_NOT_FOUND)
            )
    )
    ResponseEntity<APISuccessResponse<PlayerProfileResponse>> getProfile(
            @Parameter(description = "선수 고유 ID", example = "1") Long playerId
    );

    @Operation(
            summary = "선수 히스토리 조회",
            description = "선수 상세 페이지 히스토리 탭 전체 데이터(시즌별 스탯·시장가치 추이·성장 요약 카드)를 한 번에 반환한다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(
            responseCode = "404",
            description = "선수를 찾을 수 없거나 시즌 성적 데이터가 존재하지 않음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = APIErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "PLAYER_NOT_FOUND", value = ApiErrorExamples.PLAYER_NOT_FOUND),
                            @ExampleObject(name = "STATS_UNAVAILABLE", value = ApiErrorExamples.STATS_UNAVAILABLE)
                    }
            )
    )
    ResponseEntity<APISuccessResponse<PlayerHistoryResponse>> getRecord(
            @Parameter(description = "선수 고유 ID", example = "1") Long playerId
    );

    @Operation(
            summary = "리그별 이적 예측 조회",
            description = "특정 목적지 리그로 이적할 경우의 적응도 점수·성과 예측·시장가치 예측·유사 선수·AI 요약을 한 번에 반환한다. "
                    + "관리자가 사전 적재한 예측 캐시를 조회하므로 실시간 AI 호출 없이 응답한다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "리그 값이 올바르지 않음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = APIErrorResponse.class),
                    examples = @ExampleObject(name = "INVALID_LEAGUE", value = ApiErrorExamples.INVALID_LEAGUE)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "선수를 찾을 수 없거나 예측에 필요한 스탯 데이터가 부족함",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = APIErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "PLAYER_NOT_FOUND", value = ApiErrorExamples.PLAYER_NOT_FOUND),
                            @ExampleObject(name = "INSUFFICIENT_STATS_DATA", value = ApiErrorExamples.INSUFFICIENT_STATS_DATA)
                    }
            )
    )
    ResponseEntity<APISuccessResponse<PlayerPredictResponse>> getPredict(
            @Parameter(description = "선수 고유 ID", example = "1") Long playerId,
            @Parameter(description = "목적지 리그 코드 (EPL, LA, BL, SA, L1)", example = "EPL") String league
    );

    @Operation(
            summary = "홈 화면 추천 선수 조회",
            description = "이적 후 퍼포먼스 기대 선수 TOP N을 적응도 점수 기준으로 반환한다. "
                    + "목적지 리그 미지정 시 리그와 무관하게 적응도 점수가 높은 선수를 중복 없이 반환한다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "리그 값이 올바르지 않음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = APIErrorResponse.class),
                    examples = @ExampleObject(name = "INVALID_LEAGUE", value = ApiErrorExamples.INVALID_LEAGUE)
            )
    )
    ResponseEntity<APISuccessResponse<FeaturedPlayersResponse>> getFeaturedPlayers(
            @ParameterObject FeaturedPlayersRequest featuredPlayersRequest
    );
}
