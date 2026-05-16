package com.osondoson.backend.admin.ai.dto;

import java.util.List;

public record AiMarketValueRequest(
        List<Long> playerIds,
        String destinationLeague
) {
    public static AiMarketValueRequest of(List<Long> playerIds, String destinationLeague) {
        return new AiMarketValueRequest(playerIds, destinationLeague);
    }
}
