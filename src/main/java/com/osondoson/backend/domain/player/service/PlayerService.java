package com.osondoson.backend.domain.player.service;

import com.osondoson.backend.domain.player.dto.KeyStat;
import com.osondoson.backend.domain.player.dto.PaginationInfo;
import com.osondoson.backend.domain.player.dto.PlayerResult;
import com.osondoson.backend.domain.player.dto.StatType;
import com.osondoson.backend.domain.player.dto.request.PlayerSearchRequest;
import com.osondoson.backend.domain.player.dto.response.PlayerSearchResponse;
import com.osondoson.backend.domain.player.entity.Player;
import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;
import com.osondoson.backend.domain.player.repository.PlayerRepository;
import com.osondoson.backend.domain.player.repository.PlayerSeasonRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerSeasonRecordRepository playerSeasonRecordRepository;

    public PlayerSearchResponse search(PlayerSearchRequest request) {
        Page<Player> playersWithPage = playerRepository.searchPlayers(
                request.keyword(),
                request.league(),
                request.position(),
                request.isActive(),
                PageRequest.of(request.page() - 1, request.size())
        );

        List<Player> players = playersWithPage.getContent();

        Map<Long, PlayerSeasonRecord> latestSeasonRecordMap = getLatestSeasonRecordMap(players);
        List<PlayerResult> results = players.stream()
                .map(player -> mapToPlayerResult(player, latestSeasonRecordMap))
                .toList();

        PaginationInfo paginationInfo = PaginationInfo.of(request.page(), request.size(), playersWithPage);

        return PlayerSearchResponse.of(paginationInfo, results);
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
                StatType.searchStatsOf(player.getPosition()),
                latestSeasonRecord
        );

        return PlayerResult.of(player, keyStats);
    }
}
