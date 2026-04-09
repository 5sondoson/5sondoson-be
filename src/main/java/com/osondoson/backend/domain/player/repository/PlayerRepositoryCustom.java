package com.osondoson.backend.domain.player.repository;

import com.osondoson.backend.domain.player.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlayerRepositoryCustom {

    Page<Player> searchPlayers(
            String keyword,
            String league,
            String position,
            Boolean isActive,
            Pageable pageable
    );
}
