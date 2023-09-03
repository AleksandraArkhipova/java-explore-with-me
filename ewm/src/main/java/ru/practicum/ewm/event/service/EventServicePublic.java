package ru.practicum.ewm.event.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.core.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventSort;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.utils.EventUtils;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.CreateEndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class EventServicePublic {
    EventRepository eventRepository;
    RequestRepository requestRepository;
    EventMapper eventMapper;
    StatsClient statsClient;

    public List<EventShortDto> getAllEvents(
            String text,
            List<Long> categoryIds,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            EventSort sort,
            int from,
            int size,
            HttpServletRequest request
    ) {
        sendStatistics(request);

        List<EventDto> events = eventRepository
                .findAllByPublicFilters(text, categoryIds, paid, rangeStart, rangeEnd, sort, from, size)
                .stream()
                .map(eventMapper::eventToEventDto)
                .collect(Collectors.toList());

        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() <= event.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        if (sort == EventSort.VIEWS) {
            events.sort((event1, event2) -> Long.compare(event2.getViews(), event1.getViews()));
        }

        EventUtils.addViewsAndConfirmedRequestsToEvents(events, statsClient, requestRepository);

        return events
                .stream()
                .map(eventMapper::eventDtoToEventShortDto)
                .collect(Collectors.toList());
    }

    public EventDto getEventById(long eventId, HttpServletRequest request) {
        sendStatistics(request);

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("event", eventId));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("event", eventId);
        }

        EventDto eventDto = eventMapper.eventToEventDto(event);

        EventUtils.addViewsAndConfirmedRequestsToEvents(List.of(eventDto), statsClient, requestRepository);

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
