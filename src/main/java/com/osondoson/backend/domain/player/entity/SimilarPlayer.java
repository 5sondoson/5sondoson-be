package com.osondoson.backend.domain.player.entity;

import com.osondoson.backend.admin.ai.dto.SimilarPlayerEntry;
import com.osondoson.backend.enums.league.League;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    public static SimilarPlayer of(
            PlayerPerformancePrediction prediction,
            Long sourcePlayerId,
            League destinationLeague,
            int rank,
            Long similarPlayerId,
            Float similarityScore
    ) {
        SimilarPlayer similarPlayer = new SimilarPlayer();
        similarPlayer.prediction = prediction;
        similarPlayer.sourcePlayerId = sourcePlayerId;
        similarPlayer.destinationLeague = destinationLeague;
        similarPlayer.similarityRank = rank;
        similarPlayer.similarPlayerId = similarPlayerId;
        similarPlayer.similarityScore = similarityScore;
        similarPlayer.computedAt = LocalDateTime.now();
        return similarPlayer;
    }

    public static List<SimilarPlayer> listOf(
            PlayerPerformancePrediction performancePrediction,
            Long sourcePlayerId,
            League destinationLeague,
            List<SimilarPlayerEntry> similarPlayerEntries
    ) {
        List<SimilarPlayerEntry> sorted = similarPlayerEntries.stream()
                .sorted(Comparator.comparingDouble(SimilarPlayerEntry::similarityScore).reversed())
                .toList();

        List<SimilarPlayer> result = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            SimilarPlayerEntry similarPlayerEntry = sorted.get(i);
            result.add(
                    of(
                            performancePrediction,
                            sourcePlayerId,
                            destinationLeague,
                            i + 1,
                            similarPlayerEntry.similarPlayerId(),
                            similarPlayerEntry.similarityScore()
                    )
            );
        }
        return result;
    }
}
