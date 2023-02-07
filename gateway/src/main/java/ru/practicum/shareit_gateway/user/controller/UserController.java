package ru.practicum.shareit_gateway.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit_gateway.user.client.UserClient;
import ru.practicum.shareit_gateway.user.dto.UserDto;
import ru.practicum.shareit_gateway.validate.OnCreate;
import ru.practicum.shareit_gateway.validate.OnUpdate;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable final Long userId) {
        log.info(">>FIND USER BY ID: [" + userId + "]");
        return userClient.findById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info(">>FIND ALL USERS");
        return userClient.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> save(@Validated(OnCreate.class) @RequestBody final UserDto userDto) {
        log.info(">>SAVE USER: [" + userDto + "]");
        return userClient.save(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable final Long id,
                                         @Validated(OnUpdate.class) @RequestBody final UserDto userDto) {
        log.info(">>UPDATE USER, ID: [" + id + "]");
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable final Long userId) {
        log.info(">>DELETE USER BY ID: [" + userId + "]");
        userClient.delete(userId);
    }
}
