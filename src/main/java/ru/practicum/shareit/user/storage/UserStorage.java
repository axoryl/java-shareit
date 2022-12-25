package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User findById(Long id);

    List<User> findAll();

    User save(User user);

    User update(Long id, User user);

    void deleteById(Long id);

    void removeEmail(String email);

    void addEmail(String email);

    boolean checkIfEmailExists(String email);
}
