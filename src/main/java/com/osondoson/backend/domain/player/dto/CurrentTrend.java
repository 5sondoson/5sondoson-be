package com.osondoson.backend.domain.player.dto;

import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;
import com.osondoson.backend.enums.trend.Trend;

import java.util.List;

public record CurrentTrend(Trend trend) {

    private static final int AT_LEAST_SIZE = 2;
    private static final double TREND_CHANGE_THRESHOLD_PERCENT = 3.0;

    public static CurrentTrend of(List<PlayerSeasonRecord> records) {
        List<PlayerSeasonRecord> withMarketValue = records.stream()
                .filter(record -> record.getMarketValue() != null && record.getMarketValue() > 0)
                .toList();
        if (withMarketValue.size() >= AT_LEAST_SIZE) {
            return fromMarketValue(
                    withMarketValue.get(withMarketValue.size() - 2).getMarketValue(),
                    withMarketValue.getLast().getMarketValue()
            );
        }

        List<PlayerSeasonRecord> withRating = records.stream()
                .filter(record -> record.getStatRatingAverage() != null)
                .toList();
        if (withRating.size() >= AT_LEAST_SIZE) {
            return fromRating(
                    withRating.get(withRating.size() - 2).getStatRatingAverage(),
                    withRating.getLast().getStatRatingAverage()
            );
        }

        return new CurrentTrend(Trend.FLAT);
    }

    private static CurrentTrend fromMarketValue(long prevMarketValue, long currMarketValue) {
        double change = ((double)(currMarketValue - prevMarketValue) / prevMarketValue) * 100.0;
        return classify(change);
    }

    private static CurrentTrend fromRating(double prevRating, double currRating) {
        double change = ((currRating - prevRating) / prevRating) * 100.0;
        return classify(change);
    }

    private static CurrentTrend classify(double changePercent) {
        if (changePercent > TREND_CHANGE_THRESHOLD_PERCENT) {
            return new CurrentTrend(Trend.UP);
        }
        if (changePercent < -TREND_CHANGE_THRESHOLD_PERCENT) {
            return new CurrentTrend(Trend.DOWN);
        }
        return new CurrentTrend(Trend.FLAT);
    }
}
