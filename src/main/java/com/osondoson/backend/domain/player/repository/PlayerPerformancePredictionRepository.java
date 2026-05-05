package com.osondoson.backend.domain.player.repository;

import com.osondoson.backend.domain.player.entity.PlayerPerformancePrediction;
import com.osondoson.backend.enums.league.League;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerPerformancePredictionRepository extends JpaRepository<PlayerPerformancePrediction, Long> {

    @Query("""
            SELECT ppp FROM PlayerPerformancePrediction ppp
            JOIN FETCH ppp.player p
            LEFT JOIN FETCH p.team
            WHERE ppp.destinationLeague = :league
            ORDER BY ppp.adaptScoreTotal DESC
            """)
    List<PlayerPerformancePrediction> findTopByDestinationLeague(
            League league,
            Pageable pageable
    );

    @Query("""
            SELECT ppp FROM PlayerPerformancePrediction ppp
            JOIN FETCH ppp.player p
            LEFT JOIN FETCH p.team
            WHERE NOT EXISTS (
                SELECT 1 FROM PlayerPerformancePrediction ppp2
                WHERE ppp2.player = ppp.player
                AND (ppp2.adaptScoreTotal > ppp.adaptScoreTotal
                     OR (ppp2.adaptScoreTotal = ppp.adaptScoreTotal AND ppp2.id < ppp.id))
            )
            ORDER BY ppp.adaptScoreTotal DESC, ppp.id ASC
            """)
    List<PlayerPerformancePrediction> findTopFeatured(Pageable pageable);
}
