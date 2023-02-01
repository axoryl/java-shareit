package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.user.mapper.UserMapper.mapToUserDto;

@Transactional
@ContextConfiguration(classes = UserMapper.class)
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void updateUser_whenInvoked_thenReturnedUser() {
        final var userToUpdate = User.builder()
                .id(1L)
                .name("new name")
                .email("new_email@t.com")
                .build();
        final var foundUser = User.builder()
                .id(userToUpdate.getId())
                .name("name")
                .email("email@t.com")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(foundUser));
        when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);

        final var actualUser = userService.update(anyLong(), mapToUserDto(userToUpdate));

        assertEquals(userToUpdate.getId(), actualUser.getId());
        assertEquals(userToUpdate.getName(), actualUser.getName());
        assertEquals(userToUpdate.getEmail(), actualUser.getEmail());

        verify(userRepository).save(userToUpdate);
    }

    @Test
    void updateUser_whenUserNameIsNull_thenReturnedUser() {
        final var userToUpdate = User.builder()
                .id(1L)
                .name(null)
                .email("updated_email@t.com")
                .build();
        final var foundUser = User.builder()
                .id(1L)
                .name("name")
                .email("email@t.com")
                .build();
        final var expectedUser = User.builder()
                .id(foundUser.getId())
                .name(foundUser.getName())
                .email(userToUpdate.getEmail())
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(foundUser));
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        final var actualUser = userService.update(anyLong(), mapToUserDto(userToUpdate));

        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());

        verify(userRepository).save(expectedUser);
    }

    @Test
    void updateUser_whenUserEmailIsNull_thenReturnedUser() {
        final var userToUpdate = User.builder()
                .id(1L)
                .name("updated name")
                .email(null)
                .build();
        final var foundUser = User.builder()
                .id(1L)
                .name("name")
                .email("email@t.com")
                .build();
        final var expectedUser = User.builder()
                .id(foundUser.getId())
                .name(userToUpdate.getName())
                .email(foundUser.getEmail())
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(foundUser));
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        final var actualUser = userService.update(anyLong(), mapToUserDto(userToUpdate));

        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());

        verify(userRepository).save(expectedUser);
    }

    @Test
    void updateUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var user = UserDto.builder().build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.update(anyLong(), user));

        assertEquals(notFoundException.getMessage(), "User does not exist");
    }

    @Test
    void updateUser_whenUserEmailAlreadyExist_thenAlreadyExistsExceptionThrown() {
        final var foundUser = Optional.of(new User());
        final var updatedUser = UserDto.builder()
                .id(1L)
                .email("exist@email.com")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(foundUser);
        when(userRepository.findByEmail(updatedUser.getEmail())).thenReturn(foundUser);

        AlreadyExistsException alreadyExistsException = assertThrows(AlreadyExistsException.class,
                () -> userService.update(anyLong(), updatedUser));

        assertEquals(alreadyExistsException.getMessage(), "Email already exists");
    }
}
