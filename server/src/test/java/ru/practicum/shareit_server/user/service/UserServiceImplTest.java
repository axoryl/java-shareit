package ru.practicum.shareit_server.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit_server.exception.AlreadyExistsException;
import ru.practicum.shareit_server.exception.NotFoundException;
import ru.practicum.shareit_server.user.dto.UserDto;
import ru.practicum.shareit_server.user.model.User;
import ru.practicum.shareit_server.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit_server.user.mapper.UserMapper.mapToUser;
import static ru.practicum.shareit_server.user.mapper.UserMapper.mapToUserDto;

@Transactional
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void findUserById_whenInvoked_thenReturnedUser() {
        final var expectedUser = getUser();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(expectedUser));

        final var actualUser = userService.findById(anyLong());

        assertAll(
                () -> assertEquals(mapToUserDto(expectedUser), actualUser),
                () -> verify(userRepository).findById(anyLong())
        );
    }

    @Test
    void findUserById_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.findById(anyLong()));

        assertEquals("User does not exist", notFoundException.getMessage());
    }

    @Test
    void findAllUsers_whenInvoked_thenReturnedUsers() {
        final var expectedUsers = List.of(getUser());
        when(userRepository.findAll()).thenReturn(expectedUsers);

        final var actualUsers = userService.findAll();

        assertAll(
                () -> assertEquals(expectedUsers.size(), actualUsers.size()),
                () -> verify(userRepository).findAll()
        );
    }

    @Test
    void saveUser_whenInvoked_thenSavedAndReturnedUser() {
        final var userToSave = getUser();
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        final var savedUser = userService.save(mapToUserDto(userToSave));

        assertAll(
                () -> assertNotNull(savedUser),
                () -> assertEquals(userToSave.getName(), savedUser.getName()),
                () -> assertEquals(userToSave.getEmail(), savedUser.getEmail()),
                () -> verify(userRepository).save(any())
        );
    }

    @Test
    void updateUser_whenInvoked_thenUpdatedAndReturnedUser() {
        final var foundUser = getUser();
        final var userToUpdate = getUser();
        userToUpdate.setName("updated name");
        userToUpdate.setEmail("updatedEmail@t.com");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(foundUser));
        when(userRepository.save(any())).thenReturn(userToUpdate);

        userService.update(anyLong(), mapToUserDto(userToUpdate));

        verify(userRepository).save(userArgumentCaptor.capture());
        final var savedUser = userArgumentCaptor.getValue();

        assertAll(
                () -> assertNotNull(savedUser),
                () -> assertEquals(userToUpdate.getName(), savedUser.getName()),
                () -> assertEquals(userToUpdate.getEmail(), savedUser.getEmail())
        );
    }

    @Test
    void updateUser_whenUserNameIsNull_thenUpdatedAndReturnedUser() {
        final var foundUser = getUser();
        final var userToUpdate = getUser();
        userToUpdate.setName(null);
        userToUpdate.setEmail("new");
        final var expectedUser = getUser();
        expectedUser.setEmail("new");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(foundUser));
        when(userRepository.save(any())).thenReturn(expectedUser);

        userService.update(anyLong(), mapToUserDto(userToUpdate));

        verify(userRepository).save(userArgumentCaptor.capture());
        final var savedUser = userArgumentCaptor.getValue();

        assertAll(
                () -> assertNotNull(savedUser),
                () -> assertEquals(expectedUser.getName(), savedUser.getName()),
                () -> assertEquals(expectedUser.getEmail(), savedUser.getEmail())
        );
    }

    @Test
    void updateUser_whenUserEmailIsNull_thenReturnedUser() {
        final var foundUser = getUser();
        final var userToUpdate = getUser();
        userToUpdate.setName("new");
        userToUpdate.setEmail(null);
        final var expectedUser = getUser();
        expectedUser.setName("new");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(foundUser));
        when(userRepository.save(any())).thenReturn(expectedUser);

        userService.update(anyLong(), mapToUserDto(userToUpdate));

        verify(userRepository).save(userArgumentCaptor.capture());
        final var savedUser = userArgumentCaptor.getValue();

        assertAll(
                () -> assertNotNull(savedUser),
                () -> assertEquals(expectedUser.getName(), savedUser.getName()),
                () -> assertEquals(expectedUser.getEmail(), savedUser.getEmail())
        );
    }

    @Test
    void updateUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var user = UserDto.builder().build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.update(anyLong(), user));

        assertAll(
                () -> assertEquals("User does not exist", notFoundException.getMessage()),
                () -> verify(userRepository, never()).save(mapToUser(user))
        );
    }

    @Test
    void updateUser_whenUserEmailAlreadyExist_thenAlreadyExistsExceptionThrown() {
        final var foundUser = Optional.of(getUser());
        final var userToUpdate = getUserDto();
        when(userRepository.findById(anyLong())).thenReturn(foundUser);
        when(userRepository.findByEmail(anyString())).thenReturn(foundUser);

        AlreadyExistsException alreadyExistsException = assertThrows(AlreadyExistsException.class,
                () -> userService.update(anyLong(), userToUpdate));

        assertAll(
                () -> assertEquals("Email already exists", alreadyExistsException.getMessage()),
                () -> verify(userRepository, never()).save(mapToUser(userToUpdate))
        );
    }

    @Test
    void deleteUser() {
        userService.deleteById(anyLong());

        verify(userRepository).deleteById(anyLong());
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .name("name")
                .email("email@t.com")
                .build();
    }

    private UserDto getUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@t.com")
                .build();
    }
}
