package com.osondoson.backend.domain.player.entity;

import com.osondoson.backend.enums.league.League;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "similar_players")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SimilarPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediction_id", nullable = false)
    private PlayerPerformancePrediction prediction;

    @Column(name = "source_player_id", nullable = false)
    private Long sourcePlayerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "destination_league", nullable = false, length = 50)
    private League destinationLeague;

    @Column(name = "similarity_rank", nullable = false)
    private Integer similarityRank;

    @Column(name = "similar_player_id", nullable = false)
    private Long similarPlayerId;

    @Column(name = "similarity_score", nullable = false)
    private Float similarityScore;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;
}
