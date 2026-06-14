package com.osondoson.backend.domain.player.repository;

import com.osondoson.backend.domain.player.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long>, PlayerRepositoryCustom {

    @Query("SELECT p FROM Player p WHERE p.isActive = true")
    List<Player> findAllActive();

    @Query("""
            SELECT p FROM Player p
            LEFT JOIN FETCH p.team
            WHERE p.id IN :ids
            """)
    List<Player> findAllWithTeamByIdIn(@Param("ids") Collection<Long> ids);
}
