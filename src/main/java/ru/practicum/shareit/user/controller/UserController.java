package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validate.OnCreate;
import ru.practicum.shareit.validate.OnUpdate;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable final Long id) {
        log.info(">>FIND USER BY ID: [" + id + "]");
        return userService.findById(id);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info(">>FIND ALL USERS");
        return userService.findAll();
    }

    @PostMapping
    public UserDto save(@Validated(OnCreate.class) @RequestBody final UserDto userDto) {
        log.info(">>SAVE USER: [" + userDto + "]");
        return userService.save(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable final Long id,
                          @Validated(OnUpdate.class) @RequestBody final UserDto userDto) {
        log.info(">>UPDATE USER, ID: [" + id + "]");
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable final Long id) {
        log.info(">>DELETE USER BY ID: [" + id + "]");
        userService.deleteById(id);
    }
}
