package ru.practicum.shareit_server.user.service;

import ru.practicum.shareit_server.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto findById(Long id);

    List<UserDto> findAll();

    UserDto save(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    void deleteById(Long id);
}
