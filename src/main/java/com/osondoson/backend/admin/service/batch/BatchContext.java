package com.osondoson.backend.admin.service.batch;

import com.osondoson.backend.domain.player.entity.Player;

import java.util.List;
import java.util.Map;

public record BatchContext(
        Map<Long, Player> playerMap,
        List<List<Player>> chunks
) {}
