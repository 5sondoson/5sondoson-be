package com.osondoson.backend.domain.player.dto;

import com.osondoson.backend.domain.player.entity.PlayerPerformancePrediction;
import com.osondoson.backend.enums.position.Position;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum PredictStatType {

    GOALS_PER90("Goals/90", pred -> toDouble(pred.getPredGoalsTotalPer90())),
    SHOTS_PER90("Shots/90", pred -> toDouble(pred.getPredShotsTotalPer90())),
    SUCCESSFUL_DRIBBLES_PER90("SuccessfulDribbles/90", pred -> toDouble(pred.getPredSuccessfulDribblesPer90())),
    KEY_PASSES_PER90("KeyPass/90", pred -> toDouble(pred.getPredKeyPassesPer90())),
    PASSES_PER90("Passes/90", pred -> toDouble(pred.getPredPassesTotalPer90())),
    TACKLES_PER90("Tackles/90", pred -> toDouble(pred.getPredTacklesTotalPer90())),
    AERIALS_WON_PER90("AerialsWon/90", pred -> toDouble(pred.getPredAerielsWonPer90())),
    BLOCKS_PER90("Blocks/90", pred -> toDouble(pred.getPredBlockedShotsPer90())),
    PASS_ACCURACY("PassAccuracy", pred -> toDouble(pred.getPredAccuratePassesPct())),
    CLEAN_SHEETS("CleanSheets", pred -> toDouble(pred.getPredCleensheetsTotal()));

    private final String label;
    private final Function<PlayerPerformancePrediction, Double> extractor;

    public KeyStat extract(PlayerPerformancePrediction pred) {
        return new KeyStat(label, extractor.apply(pred));
    }

    public static List<PredictStatType> of(Position position) {
        return switch (position) {
            case FW -> List.of(GOALS_PER90, SHOTS_PER90, SUCCESSFUL_DRIBBLES_PER90);
            case MF -> List.of(KEY_PASSES_PER90, PASSES_PER90, TACKLES_PER90);
            case DF -> List.of(AERIALS_WON_PER90, BLOCKS_PER90);
            case GK -> List.of(PASS_ACCURACY, CLEAN_SHEETS);
        };
    }

    private static Double toDouble(Float f) {
        return f != null ? Double.parseDouble(f.toString()) : null;
    }
}
