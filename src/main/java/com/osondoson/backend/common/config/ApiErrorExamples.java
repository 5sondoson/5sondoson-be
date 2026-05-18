package com.osondoson.backend.common.config;

/**
 * Swagger 문서용 에러 응답 예시. 실제 {@code APIErrorResponse} 본문 형태({@code code}, {@code message})와 동일하다.
 */
public final class ApiErrorExamples {

    private ApiErrorExamples() {
    }

    public static final String INVALID_LEAGUE = """
            {
              "code": "INVALID_LEAGUE",
              "message": "리그 값이 올바르지 않습니다."
            }""";

    public static final String INVALID_POSITION = """
            {
              "code": "INVALID_POSITION",
              "message": "포지션 값이 올바르지 않습니다. 허용 값: FW, MF, DF, GK"
            }""";

    public static final String PLAYER_NOT_FOUND = """
            {
              "code": "PLAYER_NOT_FOUND",
              "message": "해당 선수를 찾을 수 없습니다."
            }""";

    public static final String STATS_UNAVAILABLE = """
            {
              "code": "STATS_UNAVAILABLE",
              "message": "해당 선수의 시즌 성적 데이터가 존재하지 않습니다."
            }""";

    public static final String INSUFFICIENT_STATS_DATA = """
            {
              "code": "INSUFFICIENT_STATS_DATA",
              "message": "해당 선수의 스탯 데이터가 부족하여 예측이 불가합니다. 최소 1시즌 이상의 데이터가 필요합니다."
            }""";

    public static final String FORBIDDEN_ADMIN_TOKEN = """
            {
              "code": "FORBIDDEN_ADMIN_TOKEN",
              "message": "어드민 토큰이 유효하지 않습니다."
            }""";
}
