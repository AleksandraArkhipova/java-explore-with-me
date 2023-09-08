package ru.practicum.ewm.event.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.core.exception.FieldValidationException;
import ru.practicum.ewm.core.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.GetEventAdminDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminDto;
import ru.practicum.ewm.core.exception.ConflictException;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.EventStateAdminAction;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.utils.EventUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class EventServiceAdmin {
    EventRepository eventRepository;
    CategoryRepository categoryRepository;
    EventMapper eventMapper;
    EventUtils utils;

    public List<EventDto> getAllEvents(
            GetEventAdminDto eventAdminDto
    ) {
        List<EventDto> eventDtos = eventRepository
                .findAllByAdminFilters(eventAdminDto)
                .stream()
                .map(eventMapper::eventToEventDto)
                .collect(Collectors.toList());

        utils.addViewsAndConfirmedRequestsToEvents(eventDtos);

        return eventDtos;
    }

    @Transactional
    public EventDto moderateEvent(long eventId, UpdateEventAdminDto dto) {

        if (dto.getEventDate() != null) {
            if (dto.getEventDate().isBefore(LocalDateTime.now())) {
                throw new FieldValidationException("EventDate", "eventDate should be in the future");
            }
        }
        Event event = checkEvent(eventId);

        eventMapper.updateEvent(event, dto);

        if (dto.getCategory() != null) {
            Category category = checkCategory(dto.getCategory());
            event.setCategory(category);
        }

        if (dto.getStateAction() != null) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException();
            }

            EventState newState = dto.getStateAction() == EventStateAdminAction.PUBLISH_EVENT
                    ? EventState.PUBLISHED
                    : EventState.CANCELED;

            event.setState(newState);
        }

        EventDto eventDto = eventMapper.eventToEventDto(event);

        utils.addViewsAndConfirmedRequestsToEvents(List.of(eventDto));

        return eventDto;
    }

    private Event checkEvent(long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException("event", eventId));
    }

    private Category checkCategory(long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new NotFoundException("category", categoryId));
    }
}
