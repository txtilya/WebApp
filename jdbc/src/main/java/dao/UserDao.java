package dao;

import model.User;

import java.util.Collection;
import java.util.Optional;


public interface UserDao {
    Collection<User> getAll();
    public Optional<User> getByEmail(String email);

    default Optional<User> getById(long id) {
        return getAll().stream()
                .filter(user -> user.getId() == id)
                .findAny();
    }
}
