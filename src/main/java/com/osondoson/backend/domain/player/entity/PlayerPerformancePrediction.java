package com.osondoson.backend.domain.player.entity;

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

    @Column(name = "adapt_score_total")
    private Integer adaptScoreTotal;

    @Column(name = "adapt_score_minutes")
    private Integer adaptScoreMinutes;

    @Column(name = "adapt_score_performance")
    private Integer adaptScorePerformance;

    @Column(name = "adapt_score_market_value")
    private Integer adaptScoreMarketValue;

    @Column(name = "adapt_score_consistency")
    private Integer adaptScoreConsistency;

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
    private Float predCleensheetsTotal;

    @Column(name = "llm_summary", columnDefinition = "TEXT")
    private String llmSummary;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;
}
