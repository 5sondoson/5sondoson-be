package com.osondoson.backend.domain.player.dto.predict;

public record AdaptScore(
        Integer minutesScore,
        Integer performanceScore,
        Integer marketValueScore,
        Integer consistencyScore
) {}
