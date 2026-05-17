package com.osondoson.backend.admin.ai.dto;

public record SimilarPlayerEntry(
        Long similarPlayerId,
        Float similarityScore
) {}
