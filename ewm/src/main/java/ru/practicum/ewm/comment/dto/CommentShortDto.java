package ru.practicum.ewm.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentShortDto {
    Long id;
    UserShortDto user;
    String text;
    LocalDateTime createdOn;
    LocalDateTime updatedOn;
}