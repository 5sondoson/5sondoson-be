package com.osondoson.backend.domain.player.dto.predict;

public record AdaptScoreResult(
        Integer total,
        AdaptScore breakdown
) {}
