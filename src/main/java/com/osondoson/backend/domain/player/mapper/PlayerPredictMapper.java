package com.osondoson.backend.domain.player.mapper;

import com.osondoson.backend.domain.player.dto.KeyStat;
import com.osondoson.backend.domain.player.dto.PlayerResult;
import com.osondoson.backend.domain.player.dto.PredictStatType;
import com.osondoson.backend.domain.player.dto.RecordStatType;
import com.osondoson.backend.domain.player.dto.predict.*;
import com.osondoson.backend.domain.player.dto.response.PlayerPredictResponse;
import com.osondoson.backend.domain.player.entity.Player;
import com.osondoson.backend.domain.player.entity.PlayerPerformancePrediction;
import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;
import com.osondoson.backend.domain.player.entity.PlayerValuePrediction;
import com.osondoson.backend.domain.player.entity.SimilarPlayer;
import com.osondoson.backend.enums.position.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PlayerPredictMapper {

    private final PlayerPredictCalculator calculator;

    public PlayerPredictResponse toResponse(
            Position position,
            PlayerSeasonRecord latestRecord,
            Long currentMarketValue,
            PlayerPerformancePrediction performancePrediction,
            PlayerValuePrediction valuePrediction,
            List<SimilarPlayer> similarPlayers,
            Map<Long, Player> similarPlayerMap,
            Map<Long, PlayerSeasonRecord> similarPlayerRecordMap
    ) {
        List<KeyStat> currentKeyStats = buildKeyStats(position, latestRecord);
        CurrentStatsResult currentStatsResult = new CurrentStatsResult(currentMarketValue, currentKeyStats);

        SimilarPlayersResult similarPlayersResult = buildSimilarPlayersResult(similarPlayers, similarPlayerMap, similarPlayerRecordMap);

        if (performancePrediction == null) {
            return PlayerPredictResponse.withCurrentOnly(currentStatsResult, similarPlayersResult);
        }

        List<KeyStat> predictedKeyStats = buildPredictedKeyStats(position, performancePrediction);
        Long predictedMv = valuePrediction != null ? valuePrediction.getPredictedMv() : null;
        PredictedStatsResult predictedStatsResult = buildPredictedStats(predictedMv, valuePrediction, predictedKeyStats);
        StatChangesResult statChangesResult = buildStatChanges(currentKeyStats, predictedKeyStats, currentMarketValue, predictedMv);
        AdaptScoreResult adaptScoreResult = buildAdaptScore(performancePrediction);
        String llmSummary = performancePrediction.getLlmSummary();

        return new PlayerPredictResponse(
                currentStatsResult,
                predictedStatsResult,
                statChangesResult,
                adaptScoreResult,
                similarPlayersResult,
                llmSummary
        );
    }

    private List<KeyStat> buildKeyStats(Position position, PlayerSeasonRecord seasonRecord) {
        if (position == null) {
            return List.of();
        }
        return RecordStatType.recordStatsOf(position).stream()
                .map(statType -> statType.extract(seasonRecord))
                .toList();
    }

    private List<KeyStat> buildPredictedKeyStats(Position position, PlayerPerformancePrediction performancePrediction) {
        return PredictStatType.of(position).stream()
                .map(statType -> statType.extract(performancePrediction))
                .toList();
    }

    private PredictedStatsResult buildPredictedStats(
            Long predictedMv,
            PlayerValuePrediction valuePrediction,
            List<KeyStat> predictedKeyStats
    ) {
        return new PredictedStatsResult(
                predictedMv,
                valuePrediction != null ? valuePrediction.getMvChangeRate() : null,
                predictedKeyStats
        );
    }

    private StatChangesResult buildStatChanges(
            List<KeyStat> currentKeyStats,
            List<KeyStat> predictedKeyStats,
            Long currentMarketValue,
            Long predictedMv
    ) {
        return new StatChangesResult(
                calculator.marketValueDelta(predictedMv, currentMarketValue),
                calculator.computeKeyStatDeltas(currentKeyStats, predictedKeyStats)
        );
    }

    private AdaptScoreResult buildAdaptScore(PlayerPerformancePrediction performancePrediction) {
        return new AdaptScoreResult(
                performancePrediction.getAdaptScoreTotal(),
                new AdaptScore(
                        performancePrediction.getAdaptScoreLeagueAdaptability(),
                        performancePrediction.getAdaptScorePerformance(),
                        performancePrediction.getAdaptScoreMarketValue(),
                        performancePrediction.getAdaptScoreConsistency()
                )
        );
    }

    private SimilarPlayersResult buildSimilarPlayersResult(
            List<SimilarPlayer> similarPlayers,
            Map<Long, Player> playerMap,
            Map<Long, PlayerSeasonRecord> seasonRecordMap
    ) {
        List<SimilarPlayerInfo> similarPlayerInfos = similarPlayers.stream()
                .map(similarPlayer -> buildSimilarPlayerInfo(
                        similarPlayer, playerMap.get(similarPlayer.getSimilarPlayerId()),
                        seasonRecordMap.get(similarPlayer.getSimilarPlayerId()))
                )
                .toList();
        return new SimilarPlayersResult(similarPlayerInfos);
    }

    private SimilarPlayerInfo buildSimilarPlayerInfo(
            SimilarPlayer similarPlayer,
            Player player,
            PlayerSeasonRecord seasonRecord
    ) {
        if (player == null) {
            return SimilarPlayerInfo.ofUnknown(
                    similarPlayer.getSimilarPlayerId(),
                    similarPlayer.getSimilarityScore()
            );
        }
        List<KeyStat> keyStats = buildKeyStats(player.getPosition(), seasonRecord);
        return new SimilarPlayerInfo(
                PlayerResult.of(player, keyStats),
                similarPlayer.getSimilarityScore()
        );
    }
}
