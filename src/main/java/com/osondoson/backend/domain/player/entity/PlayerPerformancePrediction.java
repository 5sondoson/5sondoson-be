package com.osondoson.backend.domain.player.entity;

import com.osondoson.backend.admin.ai.dto.AiPerformancePrediction;
import com.osondoson.backend.enums.league.League;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "player_performance_predictions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerPerformancePrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    @Column(name = "destination_league", nullable = false, length = 50)
    private League destinationLeague;

    // ────────────────────────────────────────── 적응도 점수  ─────────────────────────────────────────────────────

    @Column(name = "adapt_score_total")
    private Integer adaptScoreTotal;

    @Column(name = "adapt_score_league_adaptability")
    private Integer adaptScoreLeagueAdaptability;

    @Column(name = "adapt_score_performance")
    private Integer adaptScorePerformance;

    @Column(name = "adapt_score_market_value")
    private Integer adaptScoreMarketValue;

    @Column(name = "adapt_score_consistency")
    private Integer adaptScoreConsistency;

    // ────────────────────────────────────────── AI 예측 스탯 ─────────────────────────────────────────────────────

    @Column(name = "pred_goals_total_per90")
    private Float predGoalsTotalPer90;

    @Column(name = "pred_shots_total_per90")
    private Float predShotsTotalPer90;

    @Column(name = "pred_successful_dribbles_per90")
    private Float predSuccessfulDribblesPer90;

    @Column(name = "pred_key_passes_per90")
    private Float predKeyPassesPer90;

    @Column(name = "pred_passes_total_per90")
    private Float predPassesTotalPer90;

    @Column(name = "pred_tackles_total_per90")
    private Float predTacklesTotalPer90;

    @Column(name = "pred_aeriels_won_per90")
    private Float predAerielsWonPer90;

    @Column(name = "pred_blocked_shots_per90")
    private Float predBlockedShotsPer90;

    @Column(name = "pred_accurate_passes_pct")
    private Float predAccuratePassesPct;

    @Column(name = "pred_cleansheets_total")
    private Float predCleansheetsTotal;

    @Column(name = "llm_summary", columnDefinition = "TEXT")
    private String llmSummary;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;

    public static PlayerPerformancePrediction of(
            Player player,
            League destinationLeague,
            AiPerformancePrediction aiPerformancePrediction
    ) {
        PlayerPerformancePrediction performancePrediction = new PlayerPerformancePrediction();
        performancePrediction.player = player;
        performancePrediction.destinationLeague = destinationLeague;
        performancePrediction.computedAt = LocalDateTime.now();
        performancePrediction.applyPredStats(aiPerformancePrediction);
        return performancePrediction;
    }

    public void update(AiPerformancePrediction aiPerformancePrediction) {
        this.computedAt = LocalDateTime.now();
        applyPredStats(aiPerformancePrediction);
    }

    public void applyPerformanceAdaptScores(int performanceRetentionRage, int leagueAdaptability, int consistency) {
        this.adaptScorePerformance = performanceRetentionRage;
        this.adaptScoreLeagueAdaptability = leagueAdaptability;
        this.adaptScoreConsistency = consistency;
        this.adaptScoreTotal = performanceRetentionRage + leagueAdaptability + consistency;
    }

    public void applyMarketValueAdaptScore(int marketValue) {
        this.adaptScoreMarketValue = marketValue;
        this.adaptScoreTotal
                = adaptScorePerformance + adaptScoreLeagueAdaptability + adaptScoreConsistency + marketValue;
    }

    private void applyPredStats(AiPerformancePrediction aiPerformancePrediction) {
        this.predGoalsTotalPer90 = aiPerformancePrediction.predGoalsTotalPer90();
        this.predShotsTotalPer90 = aiPerformancePrediction.predShotsTotalPer90();
        this.predSuccessfulDribblesPer90 = aiPerformancePrediction.predSuccessfulDribblesPer90();
        this.predKeyPassesPer90 = aiPerformancePrediction.predKeyPassesPer90();
        this.predPassesTotalPer90 = aiPerformancePrediction.predPassesTotalPer90();
        this.predTacklesTotalPer90 = aiPerformancePrediction.predTacklesTotalPer90();
        this.predAerielsWonPer90 = aiPerformancePrediction.predAerielsWonPer90();
        this.predBlockedShotsPer90 = aiPerformancePrediction.predBlockedShotsPer90();
        this.predAccuratePassesPct = aiPerformancePrediction.predAccuratePassesPct();
        this.predCleansheetsTotal = aiPerformancePrediction.predCleensheetsTotal();
        this.llmSummary = aiPerformancePrediction.llmSummary();
    }
}
