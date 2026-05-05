package com.osondoson.backend.domain.player.dto.request;

import com.osondoson.backend.common.exception.OsondosonException;
import com.osondoson.backend.enums.league.League;
import com.osondoson.backend.enums.message.FailMessage;
import org.springframework.util.StringUtils;

public record FeaturedPlayersRequest(Integer size, String destinationLeague) {

    private static final int DEFAULT_SIZE = 5;

    public int normalizedSize() {
        if (size != null && size > 0) {
            return size;
        }
        return DEFAULT_SIZE;
    }

    public League destinationLeagueAsEnum() {
        if (!StringUtils.hasText(destinationLeague)) {
            return null;
        }
        return League.fromValue(destinationLeague.trim())
                .orElseThrow(() -> new OsondosonException(FailMessage.INVALID_LEAGUE));
    }
}
