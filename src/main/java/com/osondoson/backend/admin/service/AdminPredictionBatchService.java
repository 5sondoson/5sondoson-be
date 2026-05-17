package com.osondoson.backend.admin.service;

import com.osondoson.backend.admin.ai.AiPredictionClient;
import com.osondoson.backend.admin.ai.dto.AiMarketValuePrediction;
import com.osondoson.backend.admin.ai.dto.AiPerformancePrediction;
import com.osondoson.backend.admin.ai.dto.AiSimilarPlayersPrediction;
import com.osondoson.backend.admin.service.batch.BatchContext;
import com.osondoson.backend.admin.service.batch.BatchResult;
import com.osondoson.backend.admin.service.batch.ChunkFailurePolicy;
import com.osondoson.backend.admin.service.batch.PredictionStep;
import com.osondoson.backend.domain.player.entity.Player;
import com.osondoson.backend.domain.player.entity.PlayerPerformancePrediction;
import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;
import com.osondoson.backend.domain.player.repository.PlayerPerformancePredictionRepository;
import com.osondoson.backend.domain.player.repository.PlayerRepository;
import com.osondoson.backend.domain.player.repository.PlayerSeasonRecordRepository;
import com.osondoson.backend.enums.league.League;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPredictionBatchService {

    private final PlayerRepository playerRepository;
    private final PlayerSeasonRecordRepository seasonRecordRepository;
    private final PlayerPerformancePredictionRepository performancePredictionRepository;
    private final AiPredictionClient aiClient;
    private final PredictionPersistenceService persistenceService;

    @Value("${ai.server.chunk-size:100}")
    private int chunkSize;

    @Async("predictionExecutor")
    public void executePerformance(League destinationLeague) {
        runSingleStep(destinationLeague, PredictionStep.PERFORMANCE);
    }

    @Async("predictionExecutor")
    public void executeMarketValue(League destinationLeague) {
        runSingleStep(destinationLeague, PredictionStep.MARKET_VALUE);
    }

    @Async("predictionExecutor")
    public void executeSimilarPlayers(League destinationLeague) {
        runSingleStep(destinationLeague, PredictionStep.SIMILAR_PLAYERS);
    }

    @Async("predictionExecutor")
    public void executePipeline(League destinationLeague) {
        log.info("[전체 예측 데이터 적재] 시작 league={}", destinationLeague);

        try {
            BatchContext batchContext = loadBatchContext();

            runPipelineStep(destinationLeague, batchContext, PredictionStep.PERFORMANCE, "Step 1/3");
            runPipelineStep(destinationLeague, batchContext, PredictionStep.MARKET_VALUE, "Step 2/3");
            runPipelineStep(destinationLeague, batchContext, PredictionStep.SIMILAR_PLAYERS, "Step 3/3");

            log.info("[전체 예측 데이터 적재] 완료 league={}", destinationLeague);
        } catch (Exception e) {
            log.error("[전체 예측 데이터 적재] 실패 league={}", destinationLeague, e);
        }
    }

    private void runSingleStep(League destinationLeague, PredictionStep predictionStep) {
        log.info("[{}] 시작 league={}", predictionStep.label(), destinationLeague);

        try {
            BatchContext batchContext = loadBatchContext();
            BatchResult batchResult = runStep(
                    destinationLeague,
                    batchContext,
                    predictionStep,
                    ChunkFailurePolicy.CONTINUE
            );

            log.info("[{}] 완료 league={}, processed={}, failed={}",
                    predictionStep.label(), destinationLeague, batchResult.processed(), batchResult.failed());
        } catch (Exception e) {
            log.error("[{}] 실패 league={}", predictionStep.label(), destinationLeague, e);
        }
    }

    private void runPipelineStep(
            League destinationLeague, BatchContext batchContext, PredictionStep predictionStep, String stepOrder
    ) {
        log.info("[전체 예측 데이터 적재] {} - {} 시작 league={}", stepOrder, predictionStep.label(), destinationLeague);

        BatchResult batchResult = runStep(
                destinationLeague,
                batchContext,
                predictionStep,
                ChunkFailurePolicy.FAIL_FAST
        );

        log.info("[전체 예측 데이터 적재] {} - {} 완료 league={}, processed={}, failed={}",
                stepOrder, predictionStep.label(), destinationLeague, batchResult.processed(), batchResult.failed());
    }

    private BatchResult runStep(
            League destinationLeague,
            BatchContext batchContext,
            PredictionStep predictionStep,
            ChunkFailurePolicy failurePolicy
    ) {
        int processed = 0, failed = 0;

        for (List<Player> chunk : batchContext.chunks()) {
            List<Long> playerIds = toIds(chunk);
            try {
                processed += processChunk(
                        destinationLeague,
                        batchContext,
                        predictionStep,
                        playerIds
                );
            } catch (Exception e) {
                failed += chunk.size();
                log.error("[{}] 데이터 처리 실패 league={}, playerIds={}", predictionStep.label(), destinationLeague, playerIds, e);

                if (ChunkFailurePolicy.FAIL_FAST.equals(failurePolicy)) {
                    throw new IllegalStateException(
                            "[%s] 데이터 처리 실패 league=%s, playerIds=%s".formatted(predictionStep.label(), destinationLeague, playerIds), e);
                }
            }
        }

        return new BatchResult(processed, failed);
    }

    private int processChunk(
            League destinationLeague,
            BatchContext batchContext,
            PredictionStep predictionStep,
            List<Long> playerIds
    ) {
        return switch (predictionStep) {
            case PERFORMANCE -> processPerformanceChunk(destinationLeague, batchContext.playerMap(), playerIds);
            case MARKET_VALUE -> processMarketValueChunk(destinationLeague, batchContext.playerMap(), playerIds);
            case SIMILAR_PLAYERS -> processSimilarPlayersChunk(destinationLeague, playerIds);
        };
    }
    
    private int processPerformanceChunk(
            League destinationLeague,
            Map<Long, Player> playerMap,
            List<Long> playerIds
    ) {
        SeasonRecordMaps seasonRecordMaps = loadSeasonRecordMaps(playerIds);

        List<AiPerformancePrediction> performancePredictions =
                aiClient.fetchPerformancePredictions(playerIds, destinationLeague);

        return persistenceService.upsertPerformancePredictions(
                performancePredictions,
                destinationLeague,
                playerMap,
                seasonRecordMaps.latest(),
                seasonRecordMaps.all()
        );
    }

    private int processMarketValueChunk(
            League destinationLeague,
            Map<Long, Player> playerMap,
            List<Long> playerIds
    ) {
        Map<Long, PlayerPerformancePrediction> playerPerformancePredictionMap = loadPredictionMap(playerIds, destinationLeague);

        List<AiMarketValuePrediction> marketValuePredictions =
                aiClient.fetchMarketValuePredictions(playerIds, destinationLeague);

        return persistenceService.upsertMarketValuePredictions(
                marketValuePredictions,
                destinationLeague,
                playerMap,
                playerPerformancePredictionMap
        );
    }

    private int processSimilarPlayersChunk(League destinationLeague, List<Long> playerIds) {
        Map<Long, PlayerPerformancePrediction> playerPerformancePredictionMap = loadPredictionMap(playerIds, destinationLeague);

        List<AiSimilarPlayersPrediction> similarPlayersPredictions =
                aiClient.fetchSimilarPlayersPredictions(playerIds, destinationLeague);

        return persistenceService.replaceSimilarPlayers(
                similarPlayersPredictions,
                destinationLeague,
                playerPerformancePredictionMap
        );
    }
    
    private BatchContext loadBatchContext() {
        Map<Long, Player> playerMap = playerRepository.findAllActive().stream()
                .collect(
                        Collectors.toMap(
                                Player::getId,
                                player -> player
                        )
                );
        List<List<Player>> chunks = partition(new ArrayList<>(playerMap.values()), chunkSize);
        return new BatchContext(playerMap, chunks);
    }

    private SeasonRecordMaps loadSeasonRecordMaps(List<Long> playerIds) {
        Map<Long, List<PlayerSeasonRecord>> all = seasonRecordRepository.findAllByPlayerIdIn(playerIds)
                .stream()
                .collect(Collectors.groupingBy(seasonRecord -> seasonRecord.getPlayer().getId()));

        Map<Long, PlayerSeasonRecord> latest = all.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .max(Comparator.comparingInt(PlayerSeasonRecord::getSeasonStartYear)
                                        .thenComparingLong(PlayerSeasonRecord::getId))
                                .orElseThrow()
                ));

        return new SeasonRecordMaps(latest, all);
    }

    private Map<Long, PlayerPerformancePrediction> loadPredictionMap(List<Long> playerIds, League destinationLeague) {
        return performancePredictionRepository.findByPlayerIdsAndDestinationLeague(playerIds, destinationLeague)
                .stream()
                .collect(
                        Collectors.toMap(
                                prediction -> prediction.getPlayer().getId(),
                                prediction -> prediction)
                );
    }

    private List<Long> toIds(List<Player> players) {
        return players.stream()
                .map(Player::getId)
                .toList();
    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }
    
}
