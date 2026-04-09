package com.osondoson.backend.enums.league;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum League {

    // 비5대리그
    EREDIVISIE("eredivisie", "Eredivisie", "Netherlands", true),
    PRIMEIRA_LIGA("primeira_liga", "Primeira Liga", "Portugal", true),
    PRO_LEAGUE("pro_league", "Pro League", "Belgium", true),

    //5대리그
    PREMIER_LEAGUE("premier_league", "Premier League", "England", false),
    LA_LIGA("la_liga", "La Liga", "Spain", false),
    BUNDESLIGA("bundesliga", "Bundesliga", "Germany", false),
    SERIE_A("serie_a", "Serie A", "Italy", false),
    LIGUE_1("ligue_1", "Ligue 1", "France", false);

    private final String value;
    private final String displayName;
    private final String country;
    private final boolean sourceLeague;

    public static Optional<League> fromValue(String value) {
        if (value == null) return Optional.empty();
        return Arrays.stream(values())
                .filter(league -> league.value.equalsIgnoreCase(value))
                .findFirst();
    }

    public static Set<String> sourceLeagueKeys() {
        return Arrays.stream(values())
                .filter(League::isSourceLeague)
                .map(League::getValue)
                .collect(Collectors.toUnmodifiableSet());
    }
}
