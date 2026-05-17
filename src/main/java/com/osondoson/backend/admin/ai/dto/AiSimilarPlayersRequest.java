package com.osondoson.backend.admin.ai.dto;

import java.util.List;

public record AiSimilarPlayersRequest(
        List<Long> playerIds,
        String destinationLeague
) {
    public static AiSimilarPlayersRequest of(List<Long> playerIds, String destinationLeague) {
        return new AiSimilarPlayersRequest(playerIds, destinationLeague);
    }
}
