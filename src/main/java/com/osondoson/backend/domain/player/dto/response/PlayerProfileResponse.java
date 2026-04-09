package com.osondoson.backend.domain.player.dto.response;

import com.osondoson.backend.domain.player.entity.Player;

public record PlayerProfileResponse(
        Long playerId,
        String name,
        String position,
        String team,
        String league,
        Integer age,
        String nationality,
        Long currentMarketValue,
        String imageUrl,
        String teamLogoUrl,
        String contractExpires,
        Double height,
        Double weight
) {
    public static PlayerProfileResponse of(Player player) {
        String teamName = player.getTeam() != null ? player.getTeam().getTeamName() : null;
        String teamLogoUrl = player.getTeam() != null ? player.getTeam().getTeamLogoUrl() : null;
        String leagueKey = player.getLeague() != null ? player.getLeague().name().toLowerCase() : null;
        String position = player.getPosition() != null ? player.getPosition().name() : null;

        return new PlayerProfileResponse(
                player.getId(),
                player.getName(),
                position,
                teamName,
                leagueKey,
                player.getAge(),
                player.getNationality(),
                player.getCurrentMarketValue(),
                player.getImageUrl(),
                teamLogoUrl,
                player.getContractExpires(),
                player.getHeight(),
                player.getWeight()
        );
    }
}
