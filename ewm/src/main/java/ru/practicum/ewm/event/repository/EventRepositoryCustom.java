package ru.practicum.ewm.event.repository;

import ru.practicum.ewm.event.dto.GetEventAdminDto;
import ru.practicum.ewm.event.dto.GetEventDto;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

public interface EventRepositoryCustom {
    List<Event> findAllByAdminFilters(
            GetEventAdminDto dto
    );

    List<Event> findAllByPublicFilters(
            GetEventDto dto
    );
}
