package com.osondoson.backend.domain.player.repository;

import com.osondoson.backend.domain.player.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long>, PlayerRepositoryCustom {
}
