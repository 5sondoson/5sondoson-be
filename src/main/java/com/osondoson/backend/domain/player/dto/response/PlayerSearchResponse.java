package com.osondoson.backend.domain.player.dto.response;

import com.osondoson.backend.domain.player.dto.PaginationInfo;
import com.osondoson.backend.domain.player.dto.PlayerResult;

import java.util.List;

public record PlayerSearchResponse(
        PaginationInfo pagination,
        List<PlayerResult> results
) {
    public static PlayerSearchResponse of(PaginationInfo pagination, List<PlayerResult> results) {
        return new PlayerSearchResponse(pagination, results);
    }
}
