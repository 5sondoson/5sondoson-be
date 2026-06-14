package com.osondoson.backend.domain.player.repository;

import com.osondoson.backend.domain.player.entity.Player;
import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerSeasonRecordRepository extends JpaRepository<PlayerSeasonRecord, Long> {

    List<PlayerSeasonRecord> findByPlayerInOrderBySeasonStartYearDescIdDesc(List<Player> players);

    @Query("""
            SELECT psr FROM PlayerSeasonRecord psr
            LEFT JOIN FETCH psr.team
            WHERE psr.player = :player
            ORDER BY psr.seasonStartYear ASC
            """)
    List<PlayerSeasonRecord> findByPlayerOrderBySeasonStartYearAsc(@Param("player") Player player);

    Optional<PlayerSeasonRecord> findFirstByPlayerOrderBySeasonStartYearDescIdDesc(Player player);

    @Query(""" 
            SELECT psr FROM PlayerSeasonRecord psr 
            WHERE psr.player.id IN :playerIds 
            ORDER BY psr.player.id ASC, psr.seasonStartYear DESC, psr.id DESC
            """)
    List<PlayerSeasonRecord> findAllByPlayerIdIn(@Param("playerIds") List<Long> playerIds);
}
