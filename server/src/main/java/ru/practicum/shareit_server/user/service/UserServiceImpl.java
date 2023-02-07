package ru.practicum.shareit_server.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit_server.exception.AlreadyExistsException;
import ru.practicum.shareit_server.exception.NotFoundException;
import ru.practicum.shareit_server.user.dto.UserDto;
import ru.practicum.shareit_server.user.mapper.UserMapper;
import ru.practicum.shareit_server.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto findById(final Long id) {
        return UserMapper.mapToUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User does not exist")));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto save(final UserDto userDto) {
        return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(userDto)));
    }

    @Transactional
    @Override
    public UserDto update(final Long id, final UserDto userDto) {
        final var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User does not exist"));

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Email already exists");
        }

        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            user.setEmail(userDto.getEmail());
        }

        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void deleteById(final Long id) {
        userRepository.deleteById(id);
    }
}
