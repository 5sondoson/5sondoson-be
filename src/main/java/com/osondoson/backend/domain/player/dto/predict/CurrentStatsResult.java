package com.osondoson.backend.domain.player.dto.predict;

import com.osondoson.backend.domain.player.dto.KeyStat;

import java.util.List;

public record CurrentStatsResult(
        Long marketValue,
        List<KeyStat> keyStats
) {}
