package com.osondoson.backend.admin.service;

import com.osondoson.backend.domain.player.entity.PlayerPerformancePrediction;
import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;
import com.osondoson.backend.enums.position.Position;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 적응도 점수 산출 (총 100점)
 *
 * - 퍼포먼스 유지율    40점 : 포지션별 핵심 스탯의 예측/현재 retention rate + 나이 보정
 * - 리그 적응 효율성   20점 : 현재 패스 정확도 대비 예측 패스 정확도 유지율
 * - 시장가치 성장성    25점 : 시장가치 변화율 구간별 점수
 * - 일관성            15점 : 포지션별 2개 핵심 스탯의 평균 변동계수(CV) 기반 산출
 */

@Component
public class AdaptScoreCalculator {

    private static final int PERFORMANCE_MAX_SCORE = 40;
    private static final int LEAGUE_ADAPTABILITY_MAX_SCORE = 20;
    private static final int MARKET_VALUE_MAX_SCORE = 25;
    private static final int CONSISTENCY_MAX_SCORE = 15;

    public void applyPerformanceAdaptScores(
            PlayerPerformancePrediction performancePrediction,
            Position position,
            PlayerSeasonRecord latestRecord,
            List<PlayerSeasonRecord> allRecords,
            int age
    ) {

        int performanceRetentionRate = calculatePerformanceScore(position, performancePrediction, latestRecord, age);
        int leagueAdaptability = calculateLeagueAdaptabilityScore(performancePrediction, latestRecord);
        int consistency = calculateConsistencyScore(position, allRecords);

        performancePrediction.applyPerformanceAdaptScores(
                performanceRetentionRate,
                leagueAdaptability,
                consistency
        );
    }

    public void applyMarketValueAdaptScore(PlayerPerformancePrediction performancePrediction, Float mvChangeRate) {
        int marketValue = calculateMarketValueScore(mvChangeRate);
        performancePrediction.applyMarketValueAdaptScore(marketValue);
    }

    // ─────────────────────────────────────── 퍼포먼스 유지율 (40점) ──────────────────────────────────────────────────
    // 포지션별 핵심 스탯의 예측/현재 retention rate를 가중합산한 뒤 선수 나이에 따른 보정 factor를 적용한다.
    // 나이 보정: 어린 선수일수록 새 환경 적응력이 높다는 축구 분석 관행 반영
    //   ≤ 25세: ×1.05 (최대 40점 cap)
    //   26–29세: ×1.00
    //   30–32세: ×0.92
    //   ≥ 33세: ×0.85

    private int calculatePerformanceScore(
            Position position,
            PlayerPerformancePrediction performancePrediction,
            PlayerSeasonRecord currPerformance,
            int age
    ) {
        if (position == null || currPerformance == null) {
            return PERFORMANCE_MAX_SCORE / 2;
        }
        int rawScore = switch (position) {
            case FW -> scoreFW(performancePrediction, currPerformance);
            case MF -> scoreMF(performancePrediction, currPerformance);
            case DF -> scoreDF(performancePrediction, currPerformance);
            case GK -> scoreGK(performancePrediction, currPerformance);
        };
        return applyAgeFactor(rawScore, age);
    }

    private int applyAgeFactor(int rawScore, int age) {
        double factor;
        if (age <= 25) {
            factor = 1.05;
        }
        else if (age <= 29) {
            factor = 1.00;
        }
        else if (age <= 32) {
            factor = 0.92;
        }
        else {
            factor = 0.85;
        }
        return (int) Math.min(Math.round(rawScore * factor), PERFORMANCE_MAX_SCORE);
    }

    private int scoreFW(PlayerPerformancePrediction performancePrediction, PlayerSeasonRecord currPerformance) {
        return retentionScore(performancePrediction.getPredGoalsTotalPer90(), currPerformance.getStatGoalsTotalPer90(), 20)
             + retentionScore(performancePrediction.getPredShotsTotalPer90(), currPerformance.getStatShotsTotalTotalPer90(), 13)
             + retentionScore(performancePrediction.getPredSuccessfulDribblesPer90(), currPerformance.getStatSuccessfulDribblesTotalPer90(), 7);
    }

    private int scoreMF(PlayerPerformancePrediction performancePrediction, PlayerSeasonRecord currPerformance) {
        return retentionScore(performancePrediction.getPredKeyPassesPer90(), currPerformance.getStatKeyPassesTotalPer90(), 14)
             + retentionScore(performancePrediction.getPredPassesTotalPer90(), currPerformance.getStatPassesTotalPer90(), 14)
             + retentionScore(performancePrediction.getPredTacklesTotalPer90(), currPerformance.getStatTacklesTotalPer90(), 12);
    }

    private int scoreDF(PlayerPerformancePrediction performancePrediction, PlayerSeasonRecord currPerformance) {
        return retentionScore(performancePrediction.getPredAerielsWonPer90(), currPerformance.getStatAerielsWonTotalPer90(), 20)
             + retentionScore(performancePrediction.getPredBlockedShotsPer90(), currPerformance.getStatBlockedShotsTotalPer90(), 20);
    }

    private int scoreGK(PlayerPerformancePrediction performancePrediction, PlayerSeasonRecord currPerformance) {
        return retentionScore(performancePrediction.getPredAccuratePassesPct(), currPerformance.getStatAccuratePassesPercentageTotal(), 24)
             + retentionScore(performancePrediction.getPredCleansheetsTotal(), currPerformance.getStatCleansheetsTotal(), 16);
    }

    /**
     * retention rate → 점수 환산
     *
     * ≥ 1.0      : 만점 (예측이 현재 이상)
     * 0.7 ~ 1.0  : 선형 보간 (50% ~ 100%)
     * 0.5 ~ 0.7  : 선형 보간 (20% ~ 50%)
     * < 0.5      : 최소 10%
     */
    private int retentionScore(Float predicted, Double current, int maxScore) {
        if (predicted == null || current == null || current == 0) {
            return (int) (maxScore * 0.5);
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

    // ─────────────────────────────────────── 리그 적응 효율성 (20점) ────────────────────────────────────────────────
    // 현재 패스 정확도 대비 예측 패스 정확도의 유지율로 측정한다.
    // 새 리그에서도 현재 수준을 얼마나 유지하느냐가 적응력의 본질적 척도이다.
    // 기준값이 없으면 절대값 기반 fallback 을 사용한다.

    private int calculateLeagueAdaptabilityScore(
            PlayerPerformancePrediction performancePrediction,
            PlayerSeasonRecord currPerformance
    ) {
        Float predictionPercent = performancePrediction.getPredAccuratePassesPct();
        if (predictionPercent == null) {
            return LEAGUE_ADAPTABILITY_MAX_SCORE / 2;
        }

        Double currentPercent = (currPerformance != null) ? currPerformance.getStatAccuratePassesPercentageTotal() : null;
        if (currentPercent == null || currentPercent == 0) {
            if (predictionPercent >= 85) {
                return 17;
            }
            if (predictionPercent >= 80) {
                return 13;
            }
            if (predictionPercent >= 75) {
                return 9;
            }
            if (predictionPercent >= 70) {
                return 5;
            }
            return 2;
        }

        double retention = predictionPercent / currentPercent;
        if (retention >= 1.00) {
            return LEAGUE_ADAPTABILITY_MAX_SCORE;
        }
        if (retention >= 0.95) {
            return 17;
        }
        if (retention >= 0.90) {
            return 13;
        }
        if (retention >= 0.85) {
            return 9;
        }
        if (retention >= 0.80) {
            return 5;
        }
        return 2;
    }

    // ───────────────────────────────────────────── 시장가치 성장성 (25점) ────────────────────────────────────────────

    private int calculateMarketValueScore(Float mvChangeRate) {
        if (mvChangeRate == null) {
            return MARKET_VALUE_MAX_SCORE / 2;
        }
        float percent = mvChangeRate * 100;
        if (percent >= 30)  {
            return MARKET_VALUE_MAX_SCORE;
        }
        if (percent >= 20)  {
            return 20;
        }
        if (percent >= 10)  {
            return 15;
        }
        if (percent >= 0)   {
            return 10;
        }
        if (percent >= -10) {
            return 5;
        }
        return 0;
    }

    // ──────────────────────────────────────────────── 일관성 (15점) ───────────────────────────────────────────────────
    // 단일 스탯 CV는 샘플 노이즈에 취약하므로 포지션별 2개 핵심 스탯의 CV 평균을 사용한다.

    private int calculateConsistencyScore(Position position, List<PlayerSeasonRecord> seasonRecords) {
        if (position == null || seasonRecords == null || seasonRecords.size() < 2) {
            return CONSISTENCY_MAX_SCORE / 2;
        }

        double averageCv = averageCv(primaryStatSeriesFor(position, seasonRecords));
        if (averageCv < 0) {
            return CONSISTENCY_MAX_SCORE / 2;
        }

        if (averageCv < 0.10) {
            return CONSISTENCY_MAX_SCORE;
        }
        if (averageCv < 0.20) {
            return 12;
        }
        if (averageCv < 0.30) {
            return 9;
        }
        if (averageCv < 0.40) {
            return 6;
        }
        return 3;
    }

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
}
