package com.osondoson.backend.domain.player.dto;

import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;
import com.osondoson.backend.enums.season.Season;

import java.util.Comparator;
import java.util.List;

public record PeakSeason(
        String season,
        Long marketValue
) {

    public static PeakSeason of(List<PlayerSeasonRecord> records) {
        return records.stream()
                .filter(record -> record.getMarketValue() != null)
                .max(Comparator.comparingLong(PlayerSeasonRecord::getMarketValue))
                .map(record -> new PeakSeason(
                        Season.getSeasonNameByStartYear(record.getSeasonStartYear()),
                        record.getMarketValue()
                ))
                .orElse(null);
    }
}
