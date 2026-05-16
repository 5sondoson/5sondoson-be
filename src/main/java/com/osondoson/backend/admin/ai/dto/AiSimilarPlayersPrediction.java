package com.osondoson.backend.admin.ai.dto;

import java.util.List;

public record AiSimilarPlayersPrediction(
        Long playerId,
        List<SimilarPlayerEntry> similarPlayers
) {}
