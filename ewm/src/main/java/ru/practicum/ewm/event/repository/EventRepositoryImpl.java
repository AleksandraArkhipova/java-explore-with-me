package ru.practicum.ewm.event.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.dto.GetEventAdminDto;
import ru.practicum.ewm.event.dto.GetEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventSort;
import ru.practicum.ewm.event.model.QEvent;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventRepositoryImpl implements EventRepositoryCustom {
    EntityManager entityManager;

    @Override
    public List<Event> findAllByAdminFilters(
            GetEventAdminDto dto
    ) {
        QEvent event = QEvent.event;
        BooleanExpression where = Expressions.asBoolean(true).isTrue();

        if (dto.getRangeStart() != null && dto.getRangeEnd() != null) {
            where = where.and(event.eventDate.between(dto.getRangeStart(), dto.getRangeEnd()));
        }

        if (dto.getUserIds() != null && !dto.getUserIds().isEmpty()) {
            where = where.and(event.initiator.id.in(dto.getUserIds()));
        }

        if (dto.getStates() != null && !dto.getStates().isEmpty()) {
            where = where.and(event.state.in(dto.getStates()));
        }

        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            where = where.and(event.category.id.in(dto.getCategoryIds()));
        }

        return new JPAQuery<Event>(entityManager)
                .from(event)
                .where(where)
                .offset(dto.getFrom())
                .limit(dto.getSize())
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findAllByPublicFilters(
            GetEventDto dto
    ) {
        QEvent event = QEvent.event;
        BooleanExpression where = Expressions.asBoolean(true).isTrue();

        if (dto.getText() != null && !dto.getText().isBlank()) {
            where = where.and(event.annotation.containsIgnoreCase(dto.getText()).or(event.description.containsIgnoreCase(dto.getText())));
        }

        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            where = where.and(event.category.id.in(dto.getCategoryIds()));
        }

        if (dto.getRangeStart() == null && dto.getRangeEnd() == null) {
            where = where.and(event.eventDate.after(LocalDateTime.now()));
        }

        if (dto.getRangeStart() != null) {
            where = where.and(event.eventDate.after(dto.getRangeStart()));
        }

        if (dto.getRangeEnd() != null) {
            where = where.and(event.eventDate.before(dto.getRangeEnd()));
        }

        if (dto.getPaid() != null) {
            where = where.and(event.paid.eq(dto.getPaid()));
        }

        OrderSpecifier orderBy = event.id.asc();

        if (dto.getSort() == EventSort.EVENT_DATE) {
            orderBy = event.eventDate.desc();
        }

        return new JPAQuery<Event>(entityManager)
                .from(event)
                .where(where)
                .offset(dto.getFrom())
                .orderBy(orderBy)
                .limit(dto.getSize())
                .stream()
                .collect(Collectors.toList());
    }
}
