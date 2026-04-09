package com.osondoson.backend.domain.player.dto;

import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;

import java.util.List;

public record GrowthSummary(
        TotalMarketValueGrowth totalMarketValueGrowth,
        PeakSeason peakSeason,
        CurrentTrend currentTrend
) {
    public static GrowthSummary of(List<PlayerSeasonRecord> records) {
        return new GrowthSummary(
                TotalMarketValueGrowth.of(records),
                PeakSeason.of(records),
                CurrentTrend.of(records)
        );
    }
}
