package com.osondoson.backend.domain.player.service;

import com.osondoson.backend.common.exception.OsondosonException;
import com.osondoson.backend.domain.player.dto.FeaturedPlayerResult;
import com.osondoson.backend.domain.player.dto.KeyStat;
import com.osondoson.backend.domain.player.dto.PaginationInfo;
import com.osondoson.backend.domain.player.dto.PlayerResult;
import com.osondoson.backend.domain.player.dto.RecordStatType;
import com.osondoson.backend.domain.player.dto.response.FeaturedPlayersResponse;
import com.osondoson.backend.domain.player.dto.response.PlayerHistoryResponse;
import com.osondoson.backend.domain.player.dto.response.PlayerPredictResponse;
import com.osondoson.backend.domain.player.dto.response.PlayerProfileResponse;
import com.osondoson.backend.domain.player.dto.response.PlayerSearchResponse;
import com.osondoson.backend.domain.player.entity.Player;
import com.osondoson.backend.domain.player.entity.PlayerPerformancePrediction;
import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;
import com.osondoson.backend.domain.player.entity.PlayerValuePrediction;
import com.osondoson.backend.domain.player.entity.SimilarPlayer;
import com.osondoson.backend.domain.player.mapper.PlayerPredictMapper;
import com.osondoson.backend.domain.player.repository.PlayerPerformancePredictionRepository;
import com.osondoson.backend.domain.player.repository.PlayerRepository;
import com.osondoson.backend.domain.player.repository.PlayerSeasonRecordRepository;
import com.osondoson.backend.domain.player.repository.PlayerValuePredictionRepository;
import com.osondoson.backend.domain.player.repository.SimilarPlayerRepository;
import com.osondoson.backend.enums.league.League;
import com.osondoson.backend.enums.message.FailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerSeasonRecordRepository playerSeasonRecordRepository;
    private final PlayerValuePredictionRepository playerValuePredictionRepository;
    private final PlayerPerformancePredictionRepository playerPerformancePredictionRepository;
    private final SimilarPlayerRepository similarPlayerRepository;
    private final PlayerPredictMapper playerPredictMapper;

    public PlayerSearchResponse search(
            String keyword,
            String league,
            String position,
            int page,
            int size,
            Boolean isActive
    ) {
        Page<Player> playersWithPage = playerRepository.searchPlayers(
                keyword,
                league,
                position,
                isActive,
                PageRequest.of(page - 1, size)
        );

        List<Player> players = playersWithPage.getContent();

        Map<Long, PlayerSeasonRecord> latestSeasonRecordMap = getLatestSeasonRecordMap(players);
        List<PlayerResult> results = players.stream()
                .map(player -> mapToPlayerResult(player, latestSeasonRecordMap))
                .toList();

        PaginationInfo paginationInfo = PaginationInfo.of(page, size, playersWithPage);

        return PlayerSearchResponse.of(paginationInfo, results);
    }

    public PlayerProfileResponse getProfile(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new OsondosonException(FailMessage.PLAYER_NOT_FOUND));

        return PlayerProfileResponse.of(player);
    }

    public PlayerHistoryResponse getHistory(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new OsondosonException(FailMessage.PLAYER_NOT_FOUND));

        List<PlayerSeasonRecord> records = playerSeasonRecordRepository.findByPlayerOrderBySeasonStartYearAsc(player);
        if (records.isEmpty()) {
            throw new OsondosonException(FailMessage.STATS_UNAVAILABLE);
        }

        return PlayerHistoryResponse.of(player, records);
    }

    public PlayerPredictResponse getPredict(Long playerId, String leagueParam) {
        League league = League.fromValue(leagueParam)
                .orElseThrow(() -> new OsondosonException(FailMessage.INVALID_LEAGUE));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new OsondosonException(FailMessage.PLAYER_NOT_FOUND));

        PlayerSeasonRecord latestRecord = playerSeasonRecordRepository
                .findFirstByPlayerOrderBySeasonStartYearDescIdDesc(player)
                .orElseThrow(() -> new OsondosonException(FailMessage.INSUFFICIENT_STATS_DATA));

        PlayerPerformancePrediction performancePrediction = playerPerformancePredictionRepository
                .findByPlayerIdAndDestinationLeague(playerId, league)
                .orElse(null);

        PlayerValuePrediction valuePrediction = null;
        List<SimilarPlayer> similarPlayers = List.of();

        if (performancePrediction != null) {
            valuePrediction = playerValuePredictionRepository
                    .findByPlayerPerformancePredictionId(performancePrediction.getId())
                    .orElse(null);
            similarPlayers = similarPlayerRepository.findByPredictionIdOrderBySimilarityRankAsc(performancePrediction.getId());
        }

        Map<Long, Player> similarPlayerMap = resolveSimilarPlayerMap(similarPlayers);
        Map<Long, PlayerSeasonRecord> similarPlayerRecordMap = getLatestSeasonRecordMap(List.copyOf(similarPlayerMap.values()));

        return playerPredictMapper.toResponse(
                player.getPosition(),
                latestRecord,
                player.getCurrentMarketValue(),
                performancePrediction,
                valuePrediction,
                similarPlayers,
                similarPlayerMap,
                similarPlayerRecordMap
        );
    }

    public FeaturedPlayersResponse getFeaturedPlayers(int size, League destinationLeague) {
        List<PlayerPerformancePrediction> playerPerformancePredictions = resolveTopPredictions(size, destinationLeague);

        if (playerPerformancePredictions.isEmpty()) {
            return FeaturedPlayersResponse.empty();
        }

        Map<Long, PlayerValuePrediction> valuePredictionMap = getValuePredictionMap(playerPerformancePredictions);

        List<FeaturedPlayerResult> results = playerPerformancePredictions.stream()
                .map(prediction -> FeaturedPlayerResult.of(prediction, valuePredictionMap.get(prediction.getId())))
                .toList();

        return FeaturedPlayersResponse.of(results);
    }

    private Map<Long, PlayerSeasonRecord> getLatestSeasonRecordMap(List<Player> players) {
        if (players.isEmpty()) {
            return Map.of();
        }

        List<PlayerSeasonRecord> seasonRecords =
                playerSeasonRecordRepository.findByPlayerInOrderBySeasonStartYearDescIdDesc(players);

        Map<Long, PlayerSeasonRecord> latestSeasonRecordMap = new LinkedHashMap<>();

        for (PlayerSeasonRecord seasonRecord : seasonRecords) {
            Long playerId = seasonRecord.getPlayer().getId();
            latestSeasonRecordMap.putIfAbsent(playerId, seasonRecord);
        }

        return latestSeasonRecordMap;
    }

    private PlayerResult mapToPlayerResult(
            Player player,
            Map<Long, PlayerSeasonRecord> latestSeasonRecordMap
    ) {
        PlayerSeasonRecord latestSeasonRecord = latestSeasonRecordMap.get(player.getId());

        List<KeyStat> keyStats = player.getPosition() == null
                ? List.of()
                : KeyStat.listOf(
                RecordStatType.searchStatsOf(player.getPosition()),
                latestSeasonRecord
        );

        return PlayerResult.of(player, keyStats);
    }

    private Map<Long, Player> resolveSimilarPlayerMap(List<SimilarPlayer> similarPlayers) {
        if (similarPlayers.isEmpty()) return Map.of();
        List<Long> ids = similarPlayers.stream().map(SimilarPlayer::getSimilarPlayerId).toList();
        return playerRepository.findAllWithTeamByIdIn(ids).stream()
                .collect(Collectors.toMap(Player::getId, Function.identity()));
    }

    private List<PlayerPerformancePrediction> resolveTopPredictions(int size, League destinationLeague) {
        PageRequest pageRequest = PageRequest.of(0, size);

        if (destinationLeague == null) {
            return playerPerformancePredictionRepository.findTopFeatured(pageRequest);
        }
        return playerPerformancePredictionRepository.findTopByDestinationLeague(destinationLeague, pageRequest);
    }

    private Map<Long, PlayerValuePrediction> getValuePredictionMap(
            List<PlayerPerformancePrediction> performancePredictions
    ) {
        List<Long> predictionIds = performancePredictions.stream()
                .map(PlayerPerformancePrediction::getId)
                .toList();

        return playerValuePredictionRepository.findByPlayerPerformancePredictionIdIn(predictionIds)
                .stream()
                .collect(Collectors.toMap(
                        valuePrediction -> valuePrediction.getPlayerPerformancePrediction().getId(),
                        Function.identity()
                ));
    }
}
