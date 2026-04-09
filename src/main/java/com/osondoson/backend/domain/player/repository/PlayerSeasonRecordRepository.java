package com.osondoson.backend.domain.player.repository;

import com.osondoson.backend.domain.player.entity.Player;
import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerSeasonRecordRepository extends JpaRepository<PlayerSeasonRecord, Long> {

    List<PlayerSeasonRecord> findByPlayerInOrderBySeasonStartYearDescIdDesc(List<Player> players);

    List<PlayerSeasonRecord> findByPlayerOrderBySeasonStartYearAsc(Player player);
}
