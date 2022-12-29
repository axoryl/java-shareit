package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> usedEmails = new HashSet<>();
    private final AtomicLong id = new AtomicLong(1);

    @Override
    public User findById(final Long id) {
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(final User user) {
        user.setId(id.getAndIncrement());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(final Long id, final User user) {
        users.put(id, user);
        return user;
    }

    @Override
    public void deleteById(final Long id) {
        users.remove(id);
    }

    // Adding to the list of emails used by users
    @Override
    public void addEmail(final String email) {
        usedEmails.add(email);
    }

    // Deleting an email when deleting a user or changing an email address
    @Override
    public void removeEmail(final String email) {
        usedEmails.remove(email);
    }

    // Checking if this email is already in use
    @Override
    public boolean checkIfEmailExists(final String email) {
        return usedEmails.contains(email);
    }
}
