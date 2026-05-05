package com.osondoson.backend.domain.player.dto;

import com.osondoson.backend.domain.player.entity.PlayerPerformancePrediction;
import com.osondoson.backend.domain.player.entity.PlayerValuePrediction;

public record FeaturedPlayerResult(
        Long playerId,
        String name,
        String position,
        String team,
        String league,
        Integer age,
        Long currentMarketValue,
        String imageUrl,
        Integer adaptScore,
        Long predictedMv
) {
    public static FeaturedPlayerResult of(
            PlayerPerformancePrediction prediction,
            PlayerValuePrediction valuePrediction
    ) {
        var player = prediction.getPlayer();
        String teamName = player.getTeam() != null ? player.getTeam().getTeamName() : null;
        String league = player.getLeague() != null ? player.getLeague().getValue() : null;
        String position = player.getPosition() != null ? player.getPosition().name() : null;
        Long predictedMv = valuePrediction != null ? valuePrediction.getPredictedMv() : null;

        return new FeaturedPlayerResult(
                player.getId(),
                player.getName(),
                position,
                teamName,
                league,
                player.getAge(),
                player.getCurrentMarketValue(),
                player.getImageUrl(),
                prediction.getAdaptScoreTotal(),
                predictedMv
        );
    }
}
