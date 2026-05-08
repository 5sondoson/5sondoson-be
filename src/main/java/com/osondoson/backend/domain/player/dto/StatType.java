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

    RATING("Rating", PlayerSeasonRecord::getStatRatingAverage),
    GOALS_PER90("Goals/90", PlayerSeasonRecord::getStatGoalsTotalPer90),
    SHOTS_ON_TARGET_PER90("ShotsOnTarget/90", PlayerSeasonRecord::getStatShotsOnTargetTotal),
    SHOTS_PER90("Shots/90", PlayerSeasonRecord::getStatShotsTotalTotalPer90),
    SUCCESSFUL_DRIBBLES_PER90("SuccessfulDribbles/90", PlayerSeasonRecord::getStatSuccessfulDribblesTotalPer90),
    PASSES_PER90("Passes/90", PlayerSeasonRecord::getStatPassesTotalPer90),
    KEY_PASSES_PER90("KeyPass/90", PlayerSeasonRecord::getStatKeyPassesTotalPer90),
    TACKLES_PER90("Tackles/90", PlayerSeasonRecord::getStatTacklesTotalPer90),
    INTERCEPTIONS("Interceptions", PlayerSeasonRecord::getStatInterceptionsTotal),
    AERIALS_WON_PER90("AerialsWon/90", PlayerSeasonRecord::getStatAerielsWonTotalPer90),
    BLOCKS_PER90("Blocks/90", PlayerSeasonRecord::getStatBlockedShotsTotalPer90),
    GOALS_CONCEDED("GoalsConceded", PlayerSeasonRecord::getStatGoalsConcededTotal),
    SAVES("Saves", PlayerSeasonRecord::getStatSavesTotal),
    PASS_ACCURACY("PassAccuracy", PlayerSeasonRecord::getStatAccuratePassesPercentageTotal),
    CLEAN_SHEETS("CleanSheets", PlayerSeasonRecord::getStatCleansheetsTotal);

    private final String label;
    private final Function<PlayerSeasonRecord, Double> extractor;

    public KeyStat extract(PlayerSeasonRecord playerSeasonRecord) {
        return new KeyStat(label, extractor.apply(playerSeasonRecord));
    }

    public static List<StatType> searchStatsOf(Position position) {
        return switch (position) {
            case FW -> List.of(RATING, GOALS_PER90, SHOTS_ON_TARGET_PER90);
            case MF -> List.of(RATING, PASSES_PER90, KEY_PASSES_PER90);
            case DF -> List.of(RATING, INTERCEPTIONS, AERIALS_WON_PER90);
            case GK -> List.of(RATING, GOALS_CONCEDED, SAVES);
        };
    }

    public static List<StatType> recordStatsOf(Position position) {
        return switch (position) {
            case FW -> List.of(GOALS_PER90, SHOTS_PER90, SUCCESSFUL_DRIBBLES_PER90);
            case MF -> List.of(KEY_PASSES_PER90, PASSES_PER90, TACKLES_PER90);
            case DF -> List.of(AERIALS_WON_PER90, BLOCKS_PER90);
            case GK -> List.of(PASS_ACCURACY, CLEAN_SHEETS);
        };
    }
}
