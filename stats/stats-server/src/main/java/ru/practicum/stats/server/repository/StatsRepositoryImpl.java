package ru.practicum.stats.server.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.mapper.ViewStatsMapper;
import ru.practicum.stats.server.model.QEndpointHit;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsRepositoryImpl implements StatsRepositoryCustom {
    EntityManager entityManager;
    ViewStatsMapper viewStatsMapper;

    @Override
    public List<ViewStats> getStatisticsByUris(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        QEndpointHit hit = QEndpointHit.endpointHit;
        BooleanExpression whereExpr = hit.timestamp.between(start, end);

        if (uris != null && !uris.isEmpty()) {
            whereExpr = whereExpr.and(hit.uri.in(uris));
        }

        return new JPAQuery<Tuple>(entityManager)
                .select(hit.app, hit.uri, unique ? hit.ip.countDistinct() : Expressions.ONE.count())
                .from(hit)
                .where(whereExpr)
                .groupBy(hit.app, hit.uri)
                .orderBy(Expressions.THREE.desc())
                .stream()
                .map(viewStatsMapper::tupleToViewStats)
                .collect(Collectors.toUnmodifiableList());
    }
}