package com.osondoson.backend.domain.player.dto.response;

import com.osondoson.backend.domain.player.dto.predict.*;

public record PlayerPredictResponse(
        CurrentStatsResult currentStats,
        PredictedStatsResult predictedStats,
        StatChangesResult statChanges,
        AdaptScoreResult adaptScore,
        SimilarPlayersResult similarPlayers,
        String llmSummary
) {
    public static PlayerPredictResponse withCurrentOnly(CurrentStatsResult currentStats, SimilarPlayersResult similarPlayers) {
        return new PlayerPredictResponse(currentStats, null, null, null, similarPlayers, null);
    }
}
