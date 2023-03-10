package ru.practicum.shareit_server.user.mapper;

import ru.practicum.shareit_server.user.dto.UserDto;
import ru.practicum.shareit_server.user.dto.UserShortDto;
import ru.practicum.shareit_server.user.model.User;

public class UserMapper {

    public static UserDto mapToUserDto(final User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto mapToUserShortDto(final User user) {
        return UserShortDto.builder().id(user.getId()).build();
    }

    public static User mapToUser(final UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
