package com.osondoson.backend.admin.service;

import com.osondoson.backend.domain.player.entity.PlayerSeasonRecord;

import java.util.List;
import java.util.Map;

public record SeasonRecordMaps(
        Map<Long, PlayerSeasonRecord> latest,
        Map<Long, List<PlayerSeasonRecord>> all
) {}
