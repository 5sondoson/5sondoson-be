package com.osondoson.backend.admin.service;

import com.osondoson.backend.domain.player.entity.PlayerPerformancePrediction;
import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;
import com.osondoson.backend.enums.position.Position;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * 적응도 점수 산출 (총 100점)
 *
 * - 성과 유지율   35점 : 포지션별 핵심 스탯의 예측/현재 retention rate
 * - 시장가치 변화율 25점 : 비5대리그 선수 시장가치 변화율 분포 백분위 기반 구간 점수
 * - 성장 추세    20점 : 최근 3시즌 평점 기울기 분포 기반 구간 점수
 * - 안정성(CV)   20점 : 포지션별 분포 백분위 기반 핵심 스탯 변동계수(CV) 구간 점수
 *
 */

@Component
public class AdaptScoreCalculator {

    private static final int PERFORMANCE_MAX_SCORE = 35;
    private static final int MARKET_VALUE_MAX_SCORE = 25;
    private static final int GROWTH_TREND_MAX_SCORE = 20;
    private static final int CONSISTENCY_MAX_SCORE = 20;

    private static final double RATIO_TOP = 1.00;
    private static final double RATIO_HIGH = 0.75;
    private static final double RATIO_MID = 0.55;
    private static final double RATIO_LOW = 0.40;   // 시장가치/안정성에서는 0.35 사용
    private static final double RATIO_MV_LOW = 0.35;
    private static final double RATIO_BOTTOM = 0.20; // 시장가치/안정성에서는 0.15 사용
    private static final double RATIO_MV_BOTTOM = 0.15;

    public void applyPerformanceAdaptScores(
            PlayerPerformancePrediction performancePrediction,
            Position position,
            PlayerSeasonRecord latestRecord,
            List<PlayerSeasonRecord> allRecords
    ) {
        int performanceRetentionRate = calculatePerformanceScore(position, performancePrediction, latestRecord);
        int growthTrend = calculateGrowthTrendScore(allRecords);
        int consistency = calculateConsistencyScore(position, allRecords);

        performancePrediction.applyPerformanceAdaptScores(
                performanceRetentionRate,
                growthTrend,
                consistency
        );
    }

    public void applyMarketValueAdaptScore(PlayerPerformancePrediction performancePrediction, Float mvChangeRate) {
        int marketValue = calculateMarketValueScore(mvChangeRate);
        performancePrediction.applyMarketValueAdaptScore(marketValue);
    }

    // ─────────────────────────────────────── 성과 유지율 (35점) ──────────────────────────────────────────────────
    // 포지션별 핵심 스탯의 예측/현재 retention rate를 가중합산한다.
    // 결측 처리: 중립(50%) 처리

    private int calculatePerformanceScore(
            Position position,
            PlayerPerformancePrediction performancePrediction,
            PlayerSeasonRecord currPerformance
    ) {
        if (position == null || currPerformance == null) {
            return PERFORMANCE_MAX_SCORE / 2;
        }
        return switch (position) {
            case FW -> scoreFW(performancePrediction, currPerformance);
            case MF -> scoreMF(performancePrediction, currPerformance);
            case DF -> scoreDF(performancePrediction, currPerformance);
            case GK -> scoreGK(performancePrediction, currPerformance);
        };
    }

    private int scoreFW(PlayerPerformancePrediction performancePrediction, PlayerSeasonRecord currPerformance) {
        return retentionScore(performancePrediction.getPredGoalsTotalPer90(), currPerformance.getStatGoalsTotalPer90(), 18)
             + retentionScore(performancePrediction.getPredShotsTotalPer90(), currPerformance.getStatShotsTotalTotalPer90(), 11)
             + retentionScore(performancePrediction.getPredSuccessfulDribblesPer90(), currPerformance.getStatSuccessfulDribblesTotalPer90(), 6);
    }

    private int scoreMF(PlayerPerformancePrediction performancePrediction, PlayerSeasonRecord currPerformance) {
        return retentionScore(performancePrediction.getPredKeyPassesPer90(), currPerformance.getStatKeyPassesTotalPer90(), 12)
             + retentionScore(performancePrediction.getPredPassesTotalPer90(), currPerformance.getStatPassesTotalPer90(), 12)
             + retentionScore(performancePrediction.getPredTacklesTotalPer90(), currPerformance.getStatTacklesTotalPer90(), 11);
    }

    private int scoreDF(PlayerPerformancePrediction performancePrediction, PlayerSeasonRecord currPerformance) {
        return retentionScore(performancePrediction.getPredAerielsWonPer90(), currPerformance.getStatAerielsWonTotalPer90(), 18)
             + retentionScore(performancePrediction.getPredBlockedShotsPer90(), currPerformance.getStatBlockedShotsTotalPer90(), 17);
    }

    private int scoreGK(PlayerPerformancePrediction performancePrediction, PlayerSeasonRecord currPerformance) {
        return retentionScore(performancePrediction.getPredAccuratePassesPct(), currPerformance.getStatAccuratePassesPercentageTotal(), 21)
             + retentionScore(performancePrediction.getPredCleansheetsTotal(), currPerformance.getStatCleansheetsTotal(), 14);
    }

    /**
     * retention rate → 점수 환산 (절대 기준)
     *
     * ≥ 1.0      : 만점 (예측이 현재 이상)
     * 0.7 ~ 1.0  : 선형 보간 (50% ~ 100%)
     * 0.5 ~ 0.7  : 선형 보간 (20% ~ 50%)
     * < 0.5      : 최소 10%
     * 결측        : 중립 (50%)
     */
    private int retentionScore(Float predicted, Double current, int maxScore) {
        if (predicted == null || current == null || current == 0) {
            return (int) Math.round(maxScore * 0.5);
        }
        double retention = predicted / current;
        double ratio;
        if (retention >= 1.0) {
            ratio = 1.0;
        } else if (retention >= 0.7) {
            ratio = 0.5 + (retention - 0.7) / 0.3 * 0.5;
        } else if (retention >= 0.5) {
            ratio = 0.2 + (retention - 0.5) / 0.2 * 0.3;
        } else {
            ratio = 0.1;
        }
        return (int) Math.round(maxScore * ratio);
    }

    // ─────────────────────────────────────── 시장가치 변화율 (25점) ────────────────────────────────────────────────
    // 비5대리그 선수 시장가치 변화율 분포: P25 -33% / P50 0% / P75 +25% / P90 +100%
    //   ≥ +100%       : 100%
    //   +25% ~ +100%  : 75%
    //   0% ~ +25%     : 55%
    //   -33% ~ 0%     : 35%
    //   < -33%        : 15%
    // 결측: 중립(55%)

    private int calculateMarketValueScore(Float mvChangeRate) {
        if (mvChangeRate == null) {
            return ratioToScore(RATIO_MID, MARKET_VALUE_MAX_SCORE);
        }
        double ratio;
        if (mvChangeRate >= 1.00) {
            ratio = RATIO_TOP;
        } else if (mvChangeRate >= 0.25) {
            ratio = RATIO_HIGH;
        } else if (mvChangeRate >= 0.00) {
            ratio = RATIO_MID;
        } else if (mvChangeRate >= -0.33) {
            ratio = RATIO_MV_LOW;
        } else {
            ratio = RATIO_MV_BOTTOM;
        }
        return ratioToScore(ratio, MARKET_VALUE_MAX_SCORE);
    }

    // ─────────────────────────────────────── 성장 추세 (20점) ────────────────────────────────────────────────────
    // 최근 3시즌 평점 기울기
    // 분포: P25 -0.14 / P50 -0.03 / P75 +0.06 / P90 +0.17
    //   ≥ +0.17        : 100%
    //   +0.06 ~ +0.17  : 75%
    //   -0.03 ~ +0.06  : 55%
    //   -0.14 ~ -0.03  : 40%
    //   < -0.14        : 20%
    // 결측(유효 시즌 기록 2개 미만): 중립(55%)

    private int calculateGrowthTrendScore(List<PlayerSeasonRecord> seasonRecords) {
        Double slope = ratingSlope(seasonRecords);
        if (slope == null) {
            return ratioToScore(RATIO_MID, GROWTH_TREND_MAX_SCORE);
        }
        double ratio;
        if (slope >= 0.17) {
            ratio = RATIO_TOP;
        } else if (slope >= 0.06) {
            ratio = RATIO_HIGH;
        } else if (slope >= -0.03) {
            ratio = RATIO_MID;
        } else if (slope >= -0.14) {
            ratio = RATIO_LOW;
        } else {
            ratio = RATIO_BOTTOM;
        }
        return ratioToScore(ratio, GROWTH_TREND_MAX_SCORE);
    }

    /**
     * 최근 3시즌 평점 평균의 최소제곱 기울기.
     * 시즌 오름차순으로 정렬한 뒤 최근 3개 시즌의 rating average에 대해 단순 선형회귀 기울기를 구한다.
     */
    private Double ratingSlope(List<PlayerSeasonRecord> seasonRecords) {
        if (seasonRecords == null || seasonRecords.size() < 2) {
            return null;
        }

        List<Double> ratings = seasonRecords.stream()
                .sorted(Comparator.comparingInt(PlayerSeasonRecord::getSeasonStartYear)
                        .thenComparingLong(PlayerSeasonRecord::getId))
                .map(PlayerSeasonRecord::getStatRatingAverage)
                .filter(rating -> rating != null && rating > 0)
                .toList();

        int ratingsSize = ratings.size();
        if (ratingsSize < 2) {
            return null;
        }

        List<Double> recentRatings = ratings.subList(Math.max(0, ratingsSize - 3), ratingsSize);
        int size = recentRatings.size();

        double meanX = (size - 1) / 2.0;
        double meanY = recentRatings.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        double numerator = 0;
        double denominator = 0;
        for (int i = 0; i < size; i++) {
            double dx = i - meanX;
            numerator += dx * (recentRatings.get(i) - meanY);
            denominator += dx * dx;
        }
        if (denominator == 0) {
            return null;
        }
        return numerator / denominator;
    }

    // ──────────────────────────────────────────────── 안정성(CV) (20점) ──────────────────────────────────────────────
    // 단일 스탯 CV는 샘플 노이즈에 취약하므로 포지션별 2개 핵심 스탯의 CV 평균을 사용한다.
    // 포지션별 분포 백분위(P25/P50/P75/P90)를 기준으로 다르게 적용한다.
    // 결측(유효 시즌 기록 2개 미만): 중립(55%)

    private int calculateConsistencyScore(Position position, List<PlayerSeasonRecord> seasonRecords) {
        if (position == null || seasonRecords == null || seasonRecords.size() < 2) {
            return ratioToScore(RATIO_MID, CONSISTENCY_MAX_SCORE);
        }

        double averageCv = averageCv(primaryStatSeriesFor(position, seasonRecords));
        if (averageCv < 0) {
            return ratioToScore(RATIO_MID, CONSISTENCY_MAX_SCORE);
        }

        CvThresholds thresholds = cvThresholdsFor(position);
        double ratio;
        if (averageCv <= thresholds.top()) {
            ratio = RATIO_TOP;
        } else if (averageCv <= thresholds.high()) {
            ratio = RATIO_HIGH;
        } else if (averageCv <= thresholds.mid()) {
            ratio = RATIO_MID;
        } else if (averageCv <= thresholds.low()) {
            ratio = RATIO_MV_LOW;
        } else {
            ratio = RATIO_MV_BOTTOM;
        }
        return ratioToScore(ratio, CONSISTENCY_MAX_SCORE);
    }

    /**
     * 포지션별 CV 임계값 (상위25% / P25~P50 / P50~P75 / P75~P90 경계)
     *   FW: 0.19 / 0.30 / 0.43 / 0.57
     *   MF: 0.15 / 0.24 / 0.35 / 0.50
     *   DF: 0.20 / 0.30 / 0.42 / 0.56
     *   GK: 0.11 / 0.22 / 0.34 / 0.42
     */
    private CvThresholds cvThresholdsFor(Position position) {
        return switch (position) {
            case FW -> new CvThresholds(0.19, 0.30, 0.43, 0.57);
            case MF -> new CvThresholds(0.15, 0.24, 0.35, 0.50);
            case DF -> new CvThresholds(0.20, 0.30, 0.42, 0.56);
            case GK -> new CvThresholds(0.11, 0.22, 0.34, 0.42);
        };
    }

    private record CvThresholds(double top, double high, double mid, double low) {}

    private double averageCv(List<List<Double>> statSeries) {
        double sum = 0;
        int count = 0;

        for (List<Double> series : statSeries) {
            double cv = computeCv(series);
            if (cv >= 0) {
                sum += cv;
                count++;
            }
        }

        if (count == 0) {
            return -1;
        }
        return sum / count;
    }

    private double computeCv(List<Double> values) {
        List<Double> valid = values.stream()
                .filter(value -> value != null && value > 0)
                .toList();
        if (valid.size() < 2) {
            return -1;
        }

        double mean = valid.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
        if (mean == 0) {
            return -1;
        }

        double variance = valid.stream()
                .mapToDouble(validValue -> Math.pow(validValue - mean, 2))
                .average()
                .orElse(0);

        return Math.sqrt(variance) / mean;
    }

    private List<List<Double>> primaryStatSeriesFor(Position position, List<PlayerSeasonRecord> seasonRecords) {
        return switch (position) {
            case FW -> List.of(
                    seasonRecords.stream().map(PlayerSeasonRecord::getStatGoalsTotalPer90).toList(),
                    seasonRecords.stream().map(PlayerSeasonRecord::getStatShotsTotalTotalPer90).toList()
            );
            case MF -> List.of(
                    seasonRecords.stream().map(PlayerSeasonRecord::getStatPassesTotalPer90).toList(),
                    seasonRecords.stream().map(PlayerSeasonRecord::getStatKeyPassesTotalPer90).toList()
            );
            case DF -> List.of(
                    seasonRecords.stream().map(PlayerSeasonRecord::getStatAerielsWonTotalPer90).toList(),
                    seasonRecords.stream().map(PlayerSeasonRecord::getStatBlockedShotsTotalPer90).toList()
            );
            case GK -> List.of(
                    seasonRecords.stream().map(PlayerSeasonRecord::getStatAccuratePassesPercentageTotal).toList(),
                    seasonRecords.stream().map(PlayerSeasonRecord::getStatCleansheetsTotal).toList()
            );
        };
    }

    private int ratioToScore(double ratio, int maxScore) {
        return (int) Math.round(maxScore * ratio);
    }
}
