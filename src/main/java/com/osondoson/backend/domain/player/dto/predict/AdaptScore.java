package com.osondoson.backend.domain.player.dto.predict;

public record AdaptScore(
        Integer leagueAdaptabilityScore,
        Integer performanceScore,
        Integer marketValueScore,
        Integer consistencyScore
) {}
