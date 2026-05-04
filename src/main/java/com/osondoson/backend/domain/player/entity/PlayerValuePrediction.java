package com.osondoson.backend.domain.player.entity;

import com.osondoson.backend.enums.league.League;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "player_value_predictions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerValuePrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediction_id", nullable = false)
    private PlayerPerformancePrediction playerPerformancePrediction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    @Column(name = "destination_league", nullable = false, length = 50)
    private League destinationLeague;

    @Column(name = "predicted_mv")
    private Long predictedMv;

    @Column(name = "mv_change_rate")
    private Float mvChangeRate;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;
}
