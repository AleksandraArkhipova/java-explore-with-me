package ru.practicum.ewm.event.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.core.exception.FieldValidationException;
import ru.practicum.ewm.core.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.GetEventDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventSort;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.utils.EventUtils;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.CreateEndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServicePublic {
    EventRepository eventRepository;
    EventMapper eventMapper;
    StatsClient statsClient;
    EventUtils utils;

    public List<EventShortDto> getAllEvents(
            GetEventDto dto,
            HttpServletRequest request
    ) {

        if (dto.getRangeStart() != null && dto.getRangeEnd() != null) {
            if (dto.getRangeStart().isAfter(dto.getRangeEnd())) {
                throw new FieldValidationException("RangeStart", "rangeStart must be before RangeEnd");
            }
        }

        sendStatistics(request);

        List<EventDto> events = eventRepository
                .findAllByPublicFilters(dto)
                .stream()
                .map(eventMapper::eventToEventDto)
                .collect(Collectors.toList());

        if (dto.getOnlyAvailable()) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() <= event.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        if (dto.getSort() == EventSort.VIEWS) {
            events.sort((event1, event2) -> Long.compare(event2.getViews(), event1.getViews()));
        }

        utils.addViewsAndConfirmedRequestsToEvents(events);

        return events
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public EventDto getEventById(long eventId, HttpServletRequest request) {
        sendStatistics(request);

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("event", eventId));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("event", eventId);
        }

        EventDto eventDto = eventMapper.eventToEventDto(event);

        utils.addViewsAndConfirmedRequestsToEvents(List.of(eventDto));

        return eventDto;
    }

    private void sendStatistics(HttpServletRequest request) {
        statsClient.saveEndpointHit(new CreateEndpointHitDto(
                "ewm",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
    }
}
