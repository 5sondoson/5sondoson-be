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
}
