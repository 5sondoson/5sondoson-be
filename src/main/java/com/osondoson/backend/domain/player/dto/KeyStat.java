package com.osondoson.backend.domain.player.dto;

import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;

import java.util.List;

public record KeyStat(
        String label,
        Double value
) {

    public static List<KeyStat> listOf(List<StatType> statTypes, PlayerSeasonRecord playerSeasonRecord) {
        if (statTypes == null || playerSeasonRecord == null) {
            return List.of();
        }

        return statTypes.stream()
                .map(statType -> statType.extract(playerSeasonRecord))
                .toList();
    }
}
