package com.osondoson.backend.domain.player.dto.predict;

public record AdaptScore(
        Integer growthTrendScore,
        Integer performanceScore,
        Integer marketValueScore,
        Integer consistencyScore
) {}
