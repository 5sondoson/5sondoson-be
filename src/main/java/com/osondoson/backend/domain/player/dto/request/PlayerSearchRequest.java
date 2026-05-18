package com.osondoson.backend.domain.player.dto.request;

import com.osondoson.backend.common.exception.OsondosonException;
import com.osondoson.backend.enums.league.League;
import com.osondoson.backend.enums.position.Position;
import com.osondoson.backend.enums.message.FailMessage;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.Set;

public record PlayerSearchRequest(
        String keyword,
        @Parameter(description = "리그 필터 (ERE, PRL, BPL)") String league,
        String position,
        @Parameter(description = "페이지 번호 (1부터 시작)", required = true) int page,
        @Parameter(description = "페이지 크기", required = true) int size,
        Boolean isActive
) {
    public static final Set<String> VALID_LEAGUES = League.sourceLeagueKeys();
    public static final Set<String> VALID_POSITIONS = Position.keys();

    public PlayerSearchRequest {
        if (league != null && !VALID_LEAGUES.contains(league)) {
            throw new OsondosonException(FailMessage.INVALID_LEAGUE);
        }
        if (position != null && !VALID_POSITIONS.contains(position)) {
            throw new OsondosonException(FailMessage.INVALID_POSITION);
        }
    }
}
