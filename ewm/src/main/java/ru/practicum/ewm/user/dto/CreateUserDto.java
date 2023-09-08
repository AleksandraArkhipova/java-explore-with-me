package ru.practicum.ewm.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserDto {
    @Email
    @NotBlank
    @Size(min = 6, max = 255, message = "Email should be between 6 and 255 symbols")
    String email;

    @NotBlank
    @Size(min = 2, max = 251, message = "Name should be between 2 and 251 symbols")
    String name;
}
