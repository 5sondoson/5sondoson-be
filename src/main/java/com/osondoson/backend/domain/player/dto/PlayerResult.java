package com.osondoson.backend.domain.player.dto;

import com.osondoson.backend.domain.player.entity.Player;

import java.util.List;

public record PlayerResult(
        Long playerId,
        String name,
        String nationality,
        String position,
        String team,
        String league,
        Integer age,
        Long marketValue,
        String imageUrl,
        List<KeyStat> keyStats
) {
    public static PlayerResult of(Player player, List<KeyStat> keyStats) {
        String teamName = player.getTeam() != null ? player.getTeam().getTeamName() : null;
        String leagueKey = player.getLeague() != null ? player.getLeague().name().toLowerCase() : null;
        String position = player.getPosition() != null ? player.getPosition().name() : null;

        return new PlayerResult(
                player.getId(),
                player.getName(),
                player.getNationality(),
                position,
                teamName,
                leagueKey,
                player.getAge(),
                player.getCurrentMarketValue(),
                player.getImageUrl(),
                keyStats
        );
    }
}
