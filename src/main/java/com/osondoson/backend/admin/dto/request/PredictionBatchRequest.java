package com.osondoson.backend.admin.dto.request;

import com.osondoson.backend.common.exception.OsondosonException;
import com.osondoson.backend.enums.league.League;
import com.osondoson.backend.enums.message.FailMessage;

public record PredictionBatchRequest(
        String destinationLeague
) {
    public League destinationLeagueAsEnum() {
        return League.fromValue(destinationLeague)
                .orElseThrow(() -> new OsondosonException(FailMessage.INVALID_LEAGUE));
    }
}
