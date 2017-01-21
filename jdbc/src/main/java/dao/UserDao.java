package dao;

import model.User;

import java.util.Collection;
import java.util.Optional;


public interface UserDao {
    Collection<User> getAll();
    Collection<User> getFriends(User user);
    boolean isUsersFriends(int id, int id1);
    public Optional<User> getByEmail(String email);
    public boolean isUserExist(String login, String email);

    public void addUser(String login, String email, String pass);

    default Optional<User> getById(long id) {
        return getAll().stream()
                .filter(user -> user.getId() == id)
                .findAny();
    }

}
