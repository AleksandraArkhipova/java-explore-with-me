package ru.practicum.stats.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsDto {

    LocalDateTime start;
    LocalDateTime end;
    Optional<List<String>> uris;
    Boolean unique;
}
