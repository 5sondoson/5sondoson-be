package com.osondoson.backend.admin.ai.dto;

public record AiMarketValuePrediction(
        Long playerId,
        Long predictedMv,
        Float mvChangeRate
) {}
