package dao;

import model.User;

import java.util.Collection;
import java.util.Optional;

@FunctionalInterface
public interface UserDao {
    Collection<User> getAll();

    default Optional<User> getById(long id) {
        return getAll().stream()
                .filter(user -> user.getId() == id)
                .findAny();
    }
}
