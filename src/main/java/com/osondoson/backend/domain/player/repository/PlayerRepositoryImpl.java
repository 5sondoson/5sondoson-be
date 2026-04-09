package com.osondoson.backend.domain.player.repository;

import com.osondoson.backend.domain.player.entity.Player;
import com.osondoson.backend.enums.league.League;
import com.osondoson.backend.enums.position.Position;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.osondoson.backend.domain.player.entity.QPlayer.player;

@RequiredArgsConstructor
public class PlayerRepositoryImpl implements PlayerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Player> searchPlayers(
            String keyword,
            String league,
            String position,
            Boolean isActive,
            Pageable pageable
    ) {
        BooleanBuilder searchCondition = buildSearchCondition(keyword, league, position, isActive);

        List<Player> content = queryFactory
                .selectFrom(player)
                .leftJoin(player.team).fetchJoin()
                .where(searchCondition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(player.count())
                .from(player)
                .where(searchCondition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanBuilder buildSearchCondition(
            String keyword,
            String league,
            String position,
            Boolean isActive
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(keywordMatches(keyword));
        builder.and(leagueMatches(league));
        builder.and(positionMatches(position));
        builder.and(activeMatches(isActive));

        return builder;
    }

    private BooleanExpression keywordMatches(String keyword) {
        if (isBlank(keyword)) {
            return null;
        }
        return player.name.containsIgnoreCase(keyword)
                .or(player.team.teamName.containsIgnoreCase(keyword));
    }

    private BooleanExpression leagueMatches(String league) {
        if (isBlank(league)) {
            return null;
        }
        return League.fromValue(league)
                .map(player.league::eq)
                .orElse(null);
    }

    private BooleanExpression positionMatches(String position) {
        if (isBlank(position)) {
            return null;
        }
        return Position.from(position)
                .map(player.position::eq)
                .orElse(null);
    }

    private BooleanExpression activeMatches(Boolean isActive) {
        if (isActive == null) {
            return null;
        }
        return player.isActive.eq(isActive);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
