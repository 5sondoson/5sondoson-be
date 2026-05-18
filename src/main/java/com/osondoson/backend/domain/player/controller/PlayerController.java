package com.osondoson.backend.domain.player.controller;

import com.osondoson.backend.common.response.APISuccessResponse;
import com.osondoson.backend.domain.player.dto.request.FeaturedPlayersRequest;
import com.osondoson.backend.domain.player.dto.request.PlayerSearchRequest;
import com.osondoson.backend.domain.player.dto.response.FeaturedPlayersResponse;
import com.osondoson.backend.domain.player.dto.response.PlayerHistoryResponse;
import com.osondoson.backend.domain.player.dto.response.PlayerPredictResponse;
import com.osondoson.backend.domain.player.dto.response.PlayerProfileResponse;
import com.osondoson.backend.domain.player.dto.response.PlayerSearchResponse;
import com.osondoson.backend.domain.player.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController implements PlayerControllerSwagger {

    private final PlayerService playerService;

    @GetMapping("/search")
    public ResponseEntity<APISuccessResponse<PlayerSearchResponse>> search(
            @ModelAttribute PlayerSearchRequest playerSearchRequest
    ) {
        return APISuccessResponse.of(
                HttpStatus.OK,
                playerService.search(
                        playerSearchRequest.keyword(),
                        playerSearchRequest.league(),
                        playerSearchRequest.position(),
                        playerSearchRequest.page(),
                        playerSearchRequest.size(),
                        playerSearchRequest.isActive()
                )
        );
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<APISuccessResponse<PlayerProfileResponse>> getProfile(
            @PathVariable Long playerId
    ) {
        return APISuccessResponse.of(HttpStatus.OK, playerService.getProfile(playerId));
    }

    @GetMapping("/{playerId}/history")
    public ResponseEntity<APISuccessResponse<PlayerHistoryResponse>> getRecord(
            @PathVariable Long playerId
    ) {
        return APISuccessResponse.of(HttpStatus.OK, playerService.getHistory(playerId));
    }

    @GetMapping("/{playerId}/predict/{league}")
    public ResponseEntity<APISuccessResponse<PlayerPredictResponse>> getPredict(
            @PathVariable Long playerId,
            @PathVariable String league
    ) {
        return APISuccessResponse.of(HttpStatus.OK, playerService.getPredict(playerId, league));
    }

    @GetMapping("/featured")
    public ResponseEntity<APISuccessResponse<FeaturedPlayersResponse>> getFeaturedPlayers(
            @ModelAttribute FeaturedPlayersRequest featuredPlayersRequest
    ) {
        return APISuccessResponse.of(
                HttpStatus.OK,
                playerService.getFeaturedPlayers(
                        featuredPlayersRequest.normalizedSize(),
                        featuredPlayersRequest.destinationLeagueAsEnum()
                )
        );
    }
}
