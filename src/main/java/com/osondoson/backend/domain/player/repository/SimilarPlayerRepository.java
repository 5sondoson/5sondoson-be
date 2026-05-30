package com.osondoson.backend.domain.player.repository;

import com.osondoson.backend.domain.player.entity.SimilarPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SimilarPlayerRepository extends JpaRepository<SimilarPlayer, Long> {

    List<SimilarPlayer> findTop3ByPredictionIdOrderBySimilarityRankAsc(Long predictionId);

    @Modifying
    @Query("DELETE FROM SimilarPlayer sp WHERE sp.prediction.id = :predictionId")
    void deleteByPredictionId(@Param("predictionId") Long predictionId);
}
