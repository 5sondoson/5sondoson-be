package com.osondoson.backend.domain.player.repository;

import com.osondoson.backend.domain.player.entity.PlayerValuePrediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PlayerValuePredictionRepository extends JpaRepository<PlayerValuePrediction, Long> {

    List<PlayerValuePrediction> findByPlayerPerformancePredictionIdIn(Collection<Long> predictionIds);

    Optional<PlayerValuePrediction> findByPlayerPerformancePredictionId(Long predictionId);
}
