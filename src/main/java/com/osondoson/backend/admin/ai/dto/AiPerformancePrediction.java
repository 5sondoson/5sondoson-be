package com.osondoson.backend.admin.ai.dto;

public record AiPerformancePrediction(
        Long playerId,
        Float predGoalsTotalPer90,
        Float predShotsTotalPer90,
        Float predSuccessfulDribblesPer90,
        Float predKeyPassesPer90,
        Float predPassesTotalPer90,
        Float predTacklesTotalPer90,
        Float predAerielsWonPer90,
        Float predBlockedShotsPer90,
        Float predAccuratePassesPct,
        Float predCleensheetsTotal
) {}
