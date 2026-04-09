package com.osondoson.backend.enums.position;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Position {

    FW("Attacker"),
    MF("Midfielder"),
    DF("Defender"),
    GK("Goalkeeper");

    private final String displayName;

    public static Optional<Position> from(String value) {
        if (value == null) return Optional.empty();
        return Arrays.stream(values())
                .filter(positionType -> positionType.name().equalsIgnoreCase(value))
                .findFirst();
    }

    public static Set<String> keys() {
        return Arrays.stream(values())
                .map(Position::name)
                .collect(Collectors.toUnmodifiableSet());
    }
}
