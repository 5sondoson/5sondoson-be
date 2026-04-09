package com.osondoson.backend.domain.player.dto;

import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;

import java.util.List;

public record TotalMarketValueGrowth(Double value) {

    private static final double ROUNDING_SCALE = 10.0;
    private static final double PERCENTAGE_MULTIPLIER = 100.0;
    private static final int MINIMUM_REQUIRED_RECORD_COUNT = 2;

    public static TotalMarketValueGrowth of(List<PlayerSeasonRecord> records) {
        List<PlayerSeasonRecord> withMarketValue = filterValidMarketValueRecords(records);
        if (withMarketValue.size() < MINIMUM_REQUIRED_RECORD_COUNT) {
            return new TotalMarketValueGrowth(null);
        }

        long initialMarketValue = withMarketValue.getFirst().getMarketValue();
        long latestMarketValue = withMarketValue.getLast().getMarketValue();

        double growthPercentage = calculateGrowthPercentage(initialMarketValue, latestMarketValue);

        return new TotalMarketValueGrowth(roundToOneDecimalPlace(growthPercentage));
    }

    private static List<PlayerSeasonRecord> filterValidMarketValueRecords(List<PlayerSeasonRecord> records) {
        return records.stream()
                .filter(record -> record.getMarketValue() != null)
                .filter(record -> record.getMarketValue() > 0)
                .toList();
    }

    private static double calculateGrowthPercentage(long initialMarketValue, long latestMarketValue) {
        return ((double) (latestMarketValue - initialMarketValue) / initialMarketValue) * PERCENTAGE_MULTIPLIER;
    }

    private static double roundToOneDecimalPlace(double value) {
        return Math.round(value * ROUNDING_SCALE) / ROUNDING_SCALE;
    }
}
