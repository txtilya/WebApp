package dao;

import model.User;

import java.util.Collection;
import java.util.Optional;


public interface UserDao {
    Collection<User> getAll();
    Collection<User> getFriends(User user);
    boolean isUsersFriends(int id, int id1);
    boolean isUsersFriendsRequestExist(int id, int id1);
    void addFriend(int requesterId, int targetId);
    void delFriend(int requesterId, int targetId);
    Optional<User> getByEmail(String email);
    boolean isUserExist(String login, String email);

    void addUser(String login, String email, String pass);

    default Optional<User> getById(long id) {
        return getAll().stream()
                .filter(user -> user.getId() == id)
                .findAny();
    }

}
