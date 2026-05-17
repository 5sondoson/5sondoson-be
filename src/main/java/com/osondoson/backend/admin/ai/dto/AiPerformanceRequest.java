package com.osondoson.backend.admin.ai.dto;

import java.util.List;

public record AiPerformanceRequest(
        List<Long> playerIds,
        String destinationLeague
) {
    public static AiPerformanceRequest of(List<Long> playerIds, String destinationLeague) {
        return new AiPerformanceRequest(playerIds, destinationLeague);
    }
}
