package com.osondoson.backend.domain.player.dto.response;

import com.osondoson.backend.domain.player.dto.GrowthSummary;
import com.osondoson.backend.domain.player.dto.SeasonHistory;
import com.osondoson.backend.domain.player.entity.Player;
import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;

import java.util.List;

public record PlayerHistoryResponse(
        List<SeasonHistory> history,
        GrowthSummary growthSummary
) {
    public static PlayerHistoryResponse of(Player player, List<PlayerSeasonRecord> records) {
        List<SeasonHistory> histories = records.stream()
                .map(record -> SeasonHistory.of(record, player.getPosition()))
                .toList();

        GrowthSummary growthSummary = GrowthSummary.of(records);

        return new PlayerHistoryResponse(histories, growthSummary);
    }
}
