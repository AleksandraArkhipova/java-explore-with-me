package ru.practicum.ewm.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.model.EventSort;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetEventDto {
    String text;
    List<Long> categoryIds;
    Boolean paid;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Boolean onlyAvailable;
    EventSort sort;
    Integer from;
    Integer size;
}
