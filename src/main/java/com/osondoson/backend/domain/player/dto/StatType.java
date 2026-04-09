package com.osondoson.backend.domain.player.dto;

import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;
import com.osondoson.backend.enums.position.Position;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum StatType {

    // 선수 검색 정보 관련 지표
    GOALS_PER90("Goals/90", PlayerSeasonRecord::getStatGoalsTotalPer90),
    SHOTS_ON_TARGET("SoT/90", PlayerSeasonRecord::getStatShotsOnTargetTotal),
    PASSES_PER90("Passes/90", PlayerSeasonRecord::getStatPassesTotalPer90),
    KEY_PASSES_PER90("KP/90", PlayerSeasonRecord::getStatKeyPassesTotalPer90),
    INTERCEPTIONS("Interceptions", PlayerSeasonRecord::getStatInterceptionsTotal),
    AERIALS_WON_PER90("Aerials/90", PlayerSeasonRecord::getStatAerielsWonTotalPer90),
    GOALS_CONCEDED("GA", PlayerSeasonRecord::getStatGoalsConcededTotal),
    SAVES("Saves", PlayerSeasonRecord::getStatSavesTotal),
    RATING("Rating", PlayerSeasonRecord::getStatRatingAverage),

    // 선수 히스토리 관련 지표
    GOALS_TOTAL("Goals", PlayerSeasonRecord::getStatGoalsGoals),
    ASSISTS_TOTAL("Assists", PlayerSeasonRecord::getStatAssistsTotal),
    SHOTS_ON_TARGET_TOTAL("SoT", PlayerSeasonRecord::getStatShotsOnTargetTotal),
    KEY_PASSES_TOTAL("Key Passes", PlayerSeasonRecord::getStatKeyPassesTotal),
    PASSES_TOTAL("Passes", PlayerSeasonRecord::getStatPassesTotal),
    PASS_ACCURACY("Pass%", PlayerSeasonRecord::getStatAccuratePassesPercentageTotal),
    TACKLES_TOTAL("Tackles", PlayerSeasonRecord::getStatTacklesTotal),
    INTERCEPTIONS_TOTAL("Interceptions", PlayerSeasonRecord::getStatInterceptionsTotal),
    CLEARANCES_TOTAL("Clearances", PlayerSeasonRecord::getStatClearancesTotal),
    AERIALS_WON_TOTAL("Aerials Won", PlayerSeasonRecord::getStatAerielsWonTotal),
    SAVES_TOTAL("Saves", PlayerSeasonRecord::getStatSavesTotal),
    GOALS_CONCEDED_TOTAL("GA", PlayerSeasonRecord::getStatGoalsConcededTotal),
    CLEAN_SHEETS("Clean Sheets", PlayerSeasonRecord::getStatCleansheetsTotal);

    ;

    private final String label;
    private final Function<PlayerSeasonRecord, Double> extractor;

    public KeyStat extract(PlayerSeasonRecord playerSeasonRecord) {
        return new KeyStat(label, extractor.apply(playerSeasonRecord));
    }

    public static List<StatType> searchStatsOf(Position position) {
        return switch (position) {
            case FW -> List.of(RATING, GOALS_PER90, SHOTS_ON_TARGET);
            case MF -> List.of(RATING, PASSES_PER90, KEY_PASSES_PER90);
            case DF -> List.of(RATING, INTERCEPTIONS, AERIALS_WON_PER90);
            case GK -> List.of(RATING, GOALS_CONCEDED, SAVES);
        };
    }

    public static List<StatType> recordStatsOf(Position position) {
        return switch (position) {
            case FW -> List.of(GOALS_TOTAL, ASSISTS_TOTAL, SHOTS_ON_TARGET_TOTAL, KEY_PASSES_TOTAL);
            case MF -> List.of(ASSISTS_TOTAL, KEY_PASSES_TOTAL, PASSES_TOTAL, PASS_ACCURACY);
            case DF -> List.of(TACKLES_TOTAL, INTERCEPTIONS_TOTAL, CLEARANCES_TOTAL, AERIALS_WON_TOTAL);
            case GK -> List.of(SAVES_TOTAL, GOALS_CONCEDED_TOTAL, CLEAN_SHEETS);
        };
    }
}
