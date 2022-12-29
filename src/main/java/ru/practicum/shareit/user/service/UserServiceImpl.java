package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto findById(final Long id) {
        return UserMapper.mapToUserDto(userStorage.findById(id));
    }

    @Override
    public List<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto save(final UserDto userDto) {
        if (userStorage.checkIfEmailExists(userDto.getEmail())) {
            throw new AlreadyExistsException("Email already exists");
        }

        userStorage.addEmail(userDto.getEmail());
        final var user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userStorage.save(user));
    }

    @Override
    public UserDto update(final Long id, final UserDto userDto) {
        if (userStorage.checkIfEmailExists(userDto.getEmail())) {
            throw new AlreadyExistsException("Email already exists");
        }

        final var user = userStorage.findById(id);

        if (user == null) {
            throw new NotFoundException("User does not exist");
        }

        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            userStorage.removeEmail(user.getEmail());
            userStorage.addEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }

        return UserMapper.mapToUserDto(userStorage.update(id, user));
    }

    @Override
    public void deleteById(final Long id) {
        final var user = userStorage.findById(id);
        userStorage.removeEmail(user.getEmail());
        userStorage.deleteById(id);
    }
}
