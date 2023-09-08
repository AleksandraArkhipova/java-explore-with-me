package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "latitude", source = "location.lat")
    @Mapping(target = "longitude", source = "location.lon")
    Event createEventDtoToEvent(CreateEventDto createEventDto);

    @Mapping(target = "location.lat", source = "latitude")
    @Mapping(target = "location.lon", source = "longitude")
    EventDto eventToEventDto(Event event);

    EventShortDto toEventShortDto(EventDto eventDto);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "latitude", source = "location.lat")
    @Mapping(target = "longitude", source = "location.lon")
    void updateEvent(@MappingTarget Event event, UpdateEventAdminDto updateDto);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "latitude", source = "location.lat")
    @Mapping(target = "longitude", source = "location.lon")
    void updateEvent(@MappingTarget Event event, UpdateEventUserDto updateDto);
}
