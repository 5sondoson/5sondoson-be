package com.osondoson.backend.admin.service;

import com.osondoson.backend.admin.ai.dto.AiMarketValuePrediction;
import com.osondoson.backend.admin.ai.dto.AiPerformancePrediction;
import com.osondoson.backend.admin.ai.dto.AiSimilarPlayersPrediction;
import com.osondoson.backend.domain.player.entity.Player;
import com.osondoson.backend.domain.player.entity.PlayerPerformancePrediction;
import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;
import com.osondoson.backend.domain.player.entity.PlayerValuePrediction;
import com.osondoson.backend.domain.player.entity.SimilarPlayer;
import com.osondoson.backend.domain.player.repository.PlayerPerformancePredictionRepository;
import com.osondoson.backend.domain.player.repository.PlayerValuePredictionRepository;
import com.osondoson.backend.domain.player.repository.SimilarPlayerRepository;
import com.osondoson.backend.enums.league.League;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PredictionPersistenceService {

    private final PlayerPerformancePredictionRepository performancePredictionRepository;
    private final PlayerValuePredictionRepository valuePredictionRepository;
    private final SimilarPlayerRepository similarPlayerRepository;
    private final AdaptScoreCalculator adaptScoreCalculator;

    @Transactional
    public int upsertPerformancePredictions(
            List<AiPerformancePrediction> aiPerformancePredictions,
            League destinationLeague,
            Map<Long, Player> playerMap,
            Map<Long, PlayerSeasonRecord> latestRecordMap,
            Map<Long, List<PlayerSeasonRecord>> allRecordsMap
    ) {
        Map<Long, PlayerPerformancePrediction> existingMap =
                loadExistingPredictionMap(aiPerformancePredictions, destinationLeague);

        int processed = 0;
        for (AiPerformancePrediction aiPerformancePrediction : aiPerformancePredictions) {
            Player player = playerMap.get(aiPerformancePrediction.playerId());
            if (player == null) {
                continue;
            }

            PlayerPerformancePrediction existingPerformancePrediction = existingMap.get(aiPerformancePrediction.playerId());
            PlayerPerformancePrediction performancePrediction = resolvePerformancePrediction(
                    existingPerformancePrediction,
                    player,
                    destinationLeague,
                    aiPerformancePrediction
            );
            performancePredictionRepository.save(performancePrediction);

            adaptScoreCalculator.applyPerformanceAdaptScores(
                    performancePrediction,
                    player.getPosition(),
                    latestRecordMap.get(aiPerformancePrediction.playerId()),
                    allRecordsMap.getOrDefault(aiPerformancePrediction.playerId(), List.of()),
                    player.getAge()
            );
            processed++;
        }
        return processed;
    }

    @Transactional
    public int upsertMarketValuePredictions(
            List<AiMarketValuePrediction> marketValuePredictions,
            League destinationLeague,
            Map<Long, Player> playerMap,
            Map<Long, PlayerPerformancePrediction> performancePredictionMap
    ) {

        Map<Long, PlayerValuePrediction> existingMap = loadExistingValueMap(performancePredictionMap);

        int processed = 0;
        for (AiMarketValuePrediction marketValuePrediction : marketValuePredictions) {
            Player player = playerMap.get(marketValuePrediction.playerId());
            PlayerPerformancePrediction performancePrediction
                    = performancePredictionMap.get(marketValuePrediction.playerId());
            if (player == null || performancePrediction == null) {
                continue;
            }

            PlayerValuePrediction existingValuePrediction = existingMap.get(performancePrediction.getId());
            PlayerValuePrediction valuePrediction = resolveValuePrediction(
                    existingValuePrediction,
                    performancePrediction,
                    player,
                    destinationLeague,
                    marketValuePrediction
            );
            valuePredictionRepository.save(valuePrediction);

            adaptScoreCalculator.applyMarketValueAdaptScore(performancePrediction, marketValuePrediction.mvChangeRate());
            performancePredictionRepository.save(performancePrediction);
            processed++;
        }
        return processed;
    }

    @Transactional
    public int replaceSimilarPlayers(
            List<AiSimilarPlayersPrediction> similarPlayersPredictions,
            League destinationLeague,
            Map<Long, PlayerPerformancePrediction> performancePredictionMap
    ) {

        int processed = 0;
        for (AiSimilarPlayersPrediction similarPlayersPrediction : similarPlayersPredictions) {
            PlayerPerformancePrediction playerPerformancePrediction
                    = performancePredictionMap.get(similarPlayersPrediction.playerId());
            if (playerPerformancePrediction == null) {
                continue;
            }

            similarPlayerRepository.deleteByPredictionId(playerPerformancePrediction.getId());

            List<SimilarPlayer> similarPlayers =
                    SimilarPlayer.listOf(
                            playerPerformancePrediction,
                            similarPlayersPrediction.playerId(),
                            destinationLeague,
                            similarPlayersPrediction.similarPlayers()
                    );
            similarPlayerRepository.saveAll(similarPlayers);
            processed++;
        }
        return processed;
    }

    private Map<Long, PlayerPerformancePrediction> loadExistingPredictionMap(
            List<AiPerformancePrediction> aiPerformancePredictions,
            League destinationLeague
    ) {
        List<Long> playerIds = aiPerformancePredictions.stream()
                .map(AiPerformancePrediction::playerId)
                .toList();

        return performancePredictionRepository.findByPlayerIdsAndDestinationLeague(playerIds, destinationLeague)
                .stream()
                .collect(Collectors.toMap(
                        prediction -> prediction.getPlayer().getId(),
                        prediction -> prediction
                ));
    }

    private PlayerPerformancePrediction resolvePerformancePrediction(
            PlayerPerformancePrediction existingPerformancePrediction,
            Player player,
            League destinationLeague,
            AiPerformancePrediction aiPerformancePrediction
    ) {
        if (existingPerformancePrediction != null) {
            existingPerformancePrediction.update(aiPerformancePrediction);
            return existingPerformancePrediction;
        }
        return PlayerPerformancePrediction.of(
                player,
                destinationLeague,
                aiPerformancePrediction
        );
    }

    private Map<Long, PlayerValuePrediction> loadExistingValueMap(
            Map<Long, PlayerPerformancePrediction> performancePredictionMap
    ) {
        List<Long> predictionIds = performancePredictionMap.values().stream()
                .map(PlayerPerformancePrediction::getId)
                .toList();
        return valuePredictionRepository.findByPlayerPerformancePredictionIdIn(predictionIds)
                .stream()
                .collect(Collectors.toMap(
                        valuePrediction -> valuePrediction.getPlayerPerformancePrediction().getId(),
                        valuePrediction -> valuePrediction
                ));
    }

    private PlayerValuePrediction resolveValuePrediction(
            PlayerValuePrediction existingValuePrediction,
            PlayerPerformancePrediction performancePrediction,
            Player player,
            League destinationLeague,
            AiMarketValuePrediction marketValuePrediction
    ) {
        if (existingValuePrediction != null) {
            existingValuePrediction.update(marketValuePrediction);
            return existingValuePrediction;
        }
        return PlayerValuePrediction.of(
                performancePrediction,
                player,
                destinationLeague,
                marketValuePrediction
        );
    }
}
