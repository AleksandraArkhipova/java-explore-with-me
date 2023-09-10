package ru.practicum.stats.server.core.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FieldValidationException extends RuntimeException {
    String field;
    String description;
}