package ru.practicum.ewm.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResultDto {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
