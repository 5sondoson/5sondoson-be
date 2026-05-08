package com.osondoson.backend.enums.trend;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Trend {
    UP, DOWN, FLAT;

    @JsonValue
    public String getValue() {
        return name().toLowerCase();
    }
}
