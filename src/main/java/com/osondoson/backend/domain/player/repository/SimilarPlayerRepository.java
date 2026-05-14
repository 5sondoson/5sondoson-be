package com.osondoson.backend.domain.player.repository;

import com.osondoson.backend.domain.player.entity.SimilarPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimilarPlayerRepository extends JpaRepository<SimilarPlayer, Long> {

    List<SimilarPlayer> findByPredictionIdOrderBySimilarityRankAsc(Long predictionId);
}
