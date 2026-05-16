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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PredictionPersistenceService {

    private final PlayerPerformancePredictionRepository performancePredictionRepository;
    private final PlayerValuePredictionRepository valuePredictionRepository;
    private final SimilarPlayerRepository similarPlayerRepository;
    private final AdaptScoreCalculator adaptScoreCalculator;

    @Transactional
    public void upsertPerformancePredictions(
            List<AiPerformancePrediction> aiPerformancePredictions,
            League destinationLeague,
            Map<Long, Player> playerMap,
            Map<Long, PlayerSeasonRecord> latestRecordMap,
            Map<Long, List<PlayerSeasonRecord>> allRecordsMap
    ) {

        for (AiPerformancePrediction aiPerformancePrediction : aiPerformancePredictions) {
            Player player = playerMap.get(aiPerformancePrediction.playerId());
            if (player == null) {
                continue;
            }

            Optional<PlayerPerformancePrediction> existing =
                    performancePredictionRepository.findByPlayerIdAndDestinationLeague(
                            aiPerformancePrediction.playerId(),
                            destinationLeague
                    );

            PlayerPerformancePrediction performancePrediction;
            if (existing.isPresent()) {
                existing.get().update(aiPerformancePrediction);
                performancePrediction = existing.get();
            } else {
                performancePrediction = PlayerPerformancePrediction.of(
                        player,
                        destinationLeague,
                        aiPerformancePrediction
                );
            }

            performancePredictionRepository.save(performancePrediction);

            adaptScoreCalculator.applyPerformanceAdaptScores(
                    performancePrediction,
                    player.getPosition(),
                    latestRecordMap.get(aiPerformancePrediction.playerId()),
                    allRecordsMap.getOrDefault(aiPerformancePrediction.playerId(), List.of()),
                    player.getAge()
            );
        }
    }

    @Transactional
    public void upsertMarketValuePredictions(
            List<AiMarketValuePrediction> marketValuePredictions,
            League destinationLeague,
            Map<Long, Player> playerMap,
            Map<Long, PlayerPerformancePrediction> performancePredictionMap) {

        for (AiMarketValuePrediction marketValuePrediction : marketValuePredictions) {
            Player player = playerMap.get(marketValuePrediction.playerId());
            PlayerPerformancePrediction performancePrediction = performancePredictionMap.get(marketValuePrediction.playerId());
            if (player == null || performancePrediction == null) {
                continue;
            }

            Optional<PlayerValuePrediction> existing =
                    valuePredictionRepository.findByPlayerPerformancePredictionId(performancePrediction.getId());

            if (existing.isPresent()) {
                existing.get().update(marketValuePrediction);
                valuePredictionRepository.save(existing.get());
            } else {
                valuePredictionRepository.save(
                        PlayerValuePrediction.of(
                                performancePrediction,
                                player,
                                destinationLeague,
                                marketValuePrediction
                        )
                );
            }

            adaptScoreCalculator.applyMarketValueAdaptScore(
                    performancePrediction, marketValuePrediction.mvChangeRate());
            performancePredictionRepository.save(performancePrediction);
        }
    }

    @Transactional
    public void replaceSimilarPlayers(
            List<AiSimilarPlayersPrediction> similarPlayersPredictions,
            League destinationLeague,
            Map<Long, PlayerPerformancePrediction> performancePredictionMap
    ) {

        for (AiSimilarPlayersPrediction similarPlayersPrediction : similarPlayersPredictions) {
            PlayerPerformancePrediction playerPerformancePrediction = performancePredictionMap.get(similarPlayersPrediction.playerId());
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
        }
    }
}
