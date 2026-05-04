package com.osondoson.backend.domain.player.dto.response;

import com.osondoson.backend.domain.player.dto.FeaturedPlayerResult;

import java.util.List;

public record FeaturedPlayersResponse(List<FeaturedPlayerResult> players) {

    public static FeaturedPlayersResponse of(List<FeaturedPlayerResult> players) {
        return new FeaturedPlayersResponse(players);
    }

    public static FeaturedPlayersResponse empty() {
        return new FeaturedPlayersResponse(List.of());
    }
}
