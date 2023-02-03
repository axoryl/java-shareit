package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.mapper.UserMapper.mapToUser;
import static ru.practicum.shareit.user.mapper.UserMapper.mapToUserDto;

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

        assertEquals(mapToUserDto(expectedUser), actualUser);
        verify(userRepository).findById(anyLong());
    }

    @Test
    void findUserById_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.findById(anyLong()));

        assertEquals(notFoundException.getMessage(), "User does not exist");
    }

    @Test
    void findAllUsers_whenInvoked_thenReturnedUsers() {
        final var expectedUsers = List.of(getUser());
        when(userRepository.findAll()).thenReturn(expectedUsers);

        final var actualUsers = userService.findAll();

        assertEquals(expectedUsers.size(), actualUsers.size());
        verify(userRepository).findAll();
    }

    @Test
    void saveUser_whenInvoked_thenSavedAndReturnedUser() {
        final var userToSave = getUser();
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        final var savedUser = userService.save(mapToUserDto(userToSave));

        assertEquals(userToSave.getName(), savedUser.getName());
        assertEquals(userToSave.getEmail(), savedUser.getEmail());

        verify(userRepository).save(any());
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

        assertEquals("updated name", savedUser.getName());
        assertEquals("updatedEmail@t.com", savedUser.getEmail());
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

        assertEquals("name", savedUser.getName());
        assertEquals("new", savedUser.getEmail());
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

        assertEquals("new", savedUser.getName());
        assertEquals("email@t.com", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var user = UserDto.builder().build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.update(anyLong(), user));

        assertEquals(notFoundException.getMessage(), "User does not exist");
        verify(userRepository, never()).save(mapToUser(user));
    }

    @Test
    void updateUser_whenUserEmailAlreadyExist_thenAlreadyExistsExceptionThrown() {
        final var foundUser = Optional.of(getUser());
        final var userToUpdate = getUserDto();
        when(userRepository.findById(anyLong())).thenReturn(foundUser);
        when(userRepository.findByEmail(anyString())).thenReturn(foundUser);

        AlreadyExistsException alreadyExistsException = assertThrows(AlreadyExistsException.class,
                () -> userService.update(anyLong(), userToUpdate));

        assertEquals(alreadyExistsException.getMessage(), "Email already exists");

        verify(userRepository, never()).save(mapToUser(userToUpdate));
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
