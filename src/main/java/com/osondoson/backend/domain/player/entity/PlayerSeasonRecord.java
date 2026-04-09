package com.osondoson.backend.domain.player.entity;

import com.osondoson.backend.common.entity.BaseEntity;
import com.osondoson.backend.domain.team.Team;
import com.osondoson.backend.enums.league.League;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "player_season_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PlayerSeasonRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(name = "league", length = 50)
    private League league;

    // === 시즌 메타 데이터 ===
    @Column(name = "jersey_number")
    private Integer jerseyNumber;

    @Column(name = "season_start_year")
    private Integer seasonStartYear;

    @Column(name = "season_end_year")
    private Integer seasonEndYear;

    @Column(name = "season_duration_days")
    private Integer seasonDurationDays;

    // === 시장가치 ===
    @Column(name = "market_value")
    private Long marketValue;

    // === 선수 스탯 ===
    @Column(name = "stat_appearances_total")
    private Double statAppearancesTotal;

    @Column(name = "stat_minutes_played_total")
    private Double statMinutesPlayedTotal;

    @Column(name = "stat_goals_total")
    private Double statGoalsTotal;

    @Column(name = "stat_goals_goals")
    private Double statGoalsGoals;

    @Column(name = "stat_goals_penalties")
    private Double statGoalsPenalties;

    @Column(name = "stat_shots_total_total")
    private Double statShotsTotalTotal;

    @Column(name = "stat_shots_on_target_total")
    private Double statShotsOnTargetTotal;

    @Column(name = "stat_shots_off_target_total")
    private Double statShotsOffTargetTotal;

    @Column(name = "stat_shots_blocked_total")
    private Double statShotsBlockedTotal;

    @Column(name = "stat_successful_dribbles_total")
    private Double statSuccessfulDribblesTotal;

    @Column(name = "stat_dribble_attempts_total")
    private Double statDribbleAttemptsTotal;

    @Column(name = "stat_dribbled_past_total")
    private Double statDribbledPastTotal;

    @Column(name = "stat_key_passes_total")
    private Double statKeyPassesTotal;

    @Column(name = "stat_passes_total")
    private Double statPassesTotal;

    @Column(name = "stat_accurate_passes_total")
    private Double statAccuratePassesTotal;

    @Column(name = "stat_accurate_passes_percentage_total")
    private Double statAccuratePassesPercentageTotal;

    @Column(name = "stat_tackles_total")
    private Double statTacklesTotal;

    @Column(name = "stat_aeriels_won_total")
    private Double statAerielsWonTotal;

    @Column(name = "stat_blocked_shots_total")
    private Double statBlockedShotsTotal;

    @Column(name = "stat_clearances_total")
    private Double statClearancesTotal;

    @Column(name = "stat_interceptions_total")
    private Double statInterceptionsTotal;

    @Column(name = "stat_assists_total")
    private Double statAssistsTotal;

    @Column(name = "stat_goals_conceded_total")
    private Double statGoalsConcededTotal;

    @Column(name = "stat_own_goals_total")
    private Double statOwnGoalsTotal;

    @Column(name = "stat_cleansheets_total")
    private Double statCleansheetsTotal;

    @Column(name = "stat_cleansheets_home")
    private Double statCleansheetsHome;

    @Column(name = "stat_cleansheets_away")
    private Double statCleansheetsAway;

    @Column(name = "stat_saves_total")
    private Double statSavesTotal;

    @Column(name = "stat_saves_insidebox_total")
    private Double statSavesInsideboxTotal;

    @Column(name = "stat_fouls_total")
    private Double statFoulsTotal;

    @Column(name = "stat_fouls_drawn_total")
    private Double statFoulsDrawnTotal;

    @Column(name = "stat_dispossessed_total")
    private Double statDispossessedTotal;

    @Column(name = "stat_total_duels_total")
    private Double statTotalDuelsTotal;

    @Column(name = "stat_duels_won_total")
    private Double statDuelsWonTotal;

    @Column(name = "stat_total_crosses_total")
    private Double statTotalCrossesTotal;

    @Column(name = "stat_accurate_crosses_total")
    private Double statAccurateCrossesTotal;

    @Column(name = "stat_crosses_blocked_crosses_blocked")
    private Double statCrossesBlockedCrossesBlocked;

    @Column(name = "stat_long_balls_total")
    private Double statLongBallsTotal;

    @Column(name = "stat_long_balls_won_total")
    private Double statLongBallsWonTotal;

    @Column(name = "stat_big_chances_created_total")
    private Double statBigChancesCreatedTotal;

    @Column(name = "stat_big_chances_missed_total")
    private Double statBigChancesMissedTotal;

    @Column(name = "stat_substitutions_in")
    private Double statSubstitutionsIn;

    @Column(name = "stat_substitutions_out")
    private Double statSubstitutionsOut;

    @Column(name = "stat_lineups_total")
    private Double statLineupsTotal;

    @Column(name = "stat_bench_total")
    private Double statBenchTotal;

    @Column(name = "stat_team_wins_total")
    private Double statTeamWinsTotal;

    @Column(name = "stat_team_draws_total")
    private Double statTeamDrawsTotal;

    @Column(name = "stat_team_lost_total")
    private Double statTeamLostTotal;

    @Column(name = "stat_yellowcards_total")
    private Double statYellowcardsTotal;

    @Column(name = "stat_yellowcards_home")
    private Double statYellowcardsHome;

    @Column(name = "stat_yellowcards_away")
    private Double statYellowcardsAway;

    @Column(name = "stat_redcards_total")
    private Double statRedcardsTotal;

    @Column(name = "stat_redcards_home")
    private Double statRedcardsHome;

    @Column(name = "stat_redcards_away")
    private Double statRedcardsAway;

    @Column(name = "stat_yellowred_cards_total")
    private Double statYellowredCardsTotal;

    @Column(name = "stat_yellowred_cards_home")
    private Double statYellowredCardsHome;

    @Column(name = "stat_yellowred_cards_away")
    private Double statYellowredCardsAway;

    @Column(name = "stat_rating_average")
    private Double statRatingAverage;

    @Column(name = "stat_rating_highest")
    private Double statRatingHighest;

    @Column(name = "stat_rating_lowest")
    private Double statRatingLowest;

    @Column(name = "stat_expected_goals_expected")
    private Double statExpectedGoalsExpected;

    @Column(name = "stat_expected_goals_actual")
    private Double statExpectedGoalsActual;

    @Column(name = "stat_expected_goals_difference")
    private Double statExpectedGoalsDifference;

    @Column(name = "stat_through_balls_total")
    private Double statThroughBallsTotal;

    @Column(name = "stat_through_balls_won_total")
    private Double statThroughBallsWonTotal;

    @Column(name = "stat_hit_woodwork_total")
    private Double statHitWoodworkTotal;

    @Column(name = "stat_hattricks_total")
    private Double statHattricksTotal;

    @Column(name = "stat_hattricks_average")
    private Double statHattricksAverage;

    @Column(name = "stat_offsides_total")
    private Double statOffsidesTotal;

    @Column(name = "stat_captain_total")
    private Double statCaptainTotal;

    @Column(name = "stat_injuries_total")
    private Double statInjuriesTotal;

    @Column(name = "stat_penalties_total")
    private Double statPenaltiesTotal;

    @Column(name = "stat_penalties_won")
    private Double statPenaltiesWon;

    @Column(name = "stat_penalties_scored")
    private Double statPenaltiesScored;

    @Column(name = "stat_penalties_committed")
    private Double statPenaltiesCommitted;

    @Column(name = "stat_penalties_saved")
    private Double statPenaltiesSaved;

    @Column(name = "stat_penalties_missed")
    private Double statPenaltiesMissed;

    @Column(name = "stat_average_points_per_game_average")
    private Double statAveragePointsPerGameAverage;

    @Column(name = "stat_error_lead_to_goal_total")
    private Double statErrorLeadToGoalTotal;

    // === per90 파생 컬럼 (AI input) ===
    @Column(name = "stat_goals_total_per90")
    private Double statGoalsTotalPer90;

    @Column(name = "stat_shots_total_total_per90")
    private Double statShotsTotalTotalPer90;

    @Column(name = "stat_successful_dribbles_total_per90")
    private Double statSuccessfulDribblesTotalPer90;

    @Column(name = "stat_key_passes_total_per90")
    private Double statKeyPassesTotalPer90;

    @Column(name = "stat_passes_total_per90")
    private Double statPassesTotalPer90;

    @Column(name = "stat_tackles_total_per90")
    private Double statTacklesTotalPer90;

    @Column(name = "stat_aeriels_won_total_per90")
    private Double statAerielsWonTotalPer90;

    @Column(name = "stat_blocked_shots_total_per90")
    private Double statBlockedShotsTotalPer90;

    @Column(name = "stat_clearances_total_per90")
    private Double statClearancesTotalPer90;

}
