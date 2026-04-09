package com.osondoson.backend.domain.player.dto;

import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;
import com.osondoson.backend.enums.position.Position;
import com.osondoson.backend.enums.season.Season;

import java.util.List;

public record SeasonHistory(
        String season,
        Long marketValue,
        String club,
        String league,
        String clubLogoUrl,
        Double appearances,
        Double minutes,
        Double ratingAverage,
        List<KeyStat> keyStats
) {
    public static SeasonHistory of(PlayerSeasonRecord playerSeasonRecord, Position position) {
        String seasonLabel = Season.getSeasonNameByStartYear(playerSeasonRecord.getSeasonStartYear());

        String clubName = playerSeasonRecord.getTeam() != null ? playerSeasonRecord.getTeam().getTeamName() : null;
        String clubLogoUrl = playerSeasonRecord.getTeam() != null ? playerSeasonRecord.getTeam().getTeamLogoUrl() : null;
        String leagueKey = playerSeasonRecord.getLeague() != null ? playerSeasonRecord.getLeague().name().toLowerCase() : null;

        List<KeyStat> keyStats = KeyStat.listOf(
                StatType.recordStatsOf(position),
                playerSeasonRecord
        );

        return new SeasonHistory(
                seasonLabel,
                playerSeasonRecord.getMarketValue(),
                clubName,
                leagueKey,
                clubLogoUrl,
                playerSeasonRecord.getStatAppearancesTotal(),
                playerSeasonRecord.getStatMinutesPlayedTotal(),
                playerSeasonRecord.getStatRatingAverage(),
                keyStats
        );
    }
}
