package com.osondoson.backend.domain.player.controller;

import com.osondoson.backend.common.response.APISuccessResponse;
import com.osondoson.backend.domain.player.dto.request.PlayerSearchRequest;
import com.osondoson.backend.domain.player.dto.response.PlayerHistoryResponse;
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
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/search")
    public ResponseEntity<APISuccessResponse<PlayerSearchResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String league,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int size,
            @RequestParam(required = false) Boolean isActive
    ) {
        PlayerSearchRequest request = new PlayerSearchRequest(
                keyword,
                league,
                position,
                page,
                size,
                isActive
        );
        return APISuccessResponse.of(HttpStatus.OK, playerService.search(request));
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
}
