package ru.practicum.shareit_gateway.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit_gateway.validate.OnCreate;
import ru.practicum.shareit_gateway.validate.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {

    private Long id;

    @NotBlank(groups = OnCreate.class)
    private String name;

    @NotBlank(groups = OnCreate.class)
    @Email(groups = {OnCreate.class, OnUpdate.class})
    private String email;
}
