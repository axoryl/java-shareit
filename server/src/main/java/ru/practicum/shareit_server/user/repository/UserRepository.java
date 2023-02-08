package ru.practicum.shareit_server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit_server.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
