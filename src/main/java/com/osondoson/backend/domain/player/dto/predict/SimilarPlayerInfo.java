package com.osondoson.backend.domain.player.dto.predict;

import com.osondoson.backend.domain.player.dto.PlayerResult;

import java.util.List;

public record SimilarPlayerInfo(PlayerResult player, Float similarityScore) {

    public static SimilarPlayerInfo ofUnknown(Long playerId, Float similarityScore) {
        return new SimilarPlayerInfo(
                new PlayerResult(
                        playerId,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of()
                ),
                similarityScore
        );
    }
}
