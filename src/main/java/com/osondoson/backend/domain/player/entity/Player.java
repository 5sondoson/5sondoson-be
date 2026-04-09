package com.osondoson.backend.domain.player.entity;

import com.osondoson.backend.common.entity.BaseEntity;
import com.osondoson.backend.enums.position.Position;
import com.osondoson.backend.enums.league.League;
import com.osondoson.backend.domain.team.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "players")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false, length = 20)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(name = "league", length = 50)
    private League league;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "nationality_id")
    private Long nationalityId;

    @Column(name = "nationality", length = 10)
    private String nationality;

    @Column(name = "country_id")
    private Long countryId;

    @Column(name = "height")
    private Double height;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "contract_expires", length = 10)
    private String contractExpires;

    @Column(name = "current_market_value")
    private Long currentMarketValue;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
