package dao;

import model.User;

import java.util.Collection;
import java.util.Optional;


public interface UserDao {

    default Optional<User> getById(long id) {
        return getAll().stream()
                .filter(user -> user.getId() == id)
                .findAny();
    }
    Collection<User> getAll();
    Collection<User> getFriends(User user);
    Optional<User> getByEmail(String email);

    boolean isUsersFriends(int id, int id1);
    boolean isUsersFriendsRequestExist(int id, int id1);
    void addFriend(int requesterId, int targetId);
    void delFriend(int requesterId, int targetId);
    boolean isUserExist(String login, String email);
    void addUser(String login, String email, String pass);
    int getOrCreateDialogId(int requesterId, int targetId);
    int getDialog(int requesterId, int targetId);
    int createDialogAndAddUsers(int requesterId, int targetId);
    void addUserToConference(int conferenceId, int userId);
    int createDialog();
    int getIdForDialog();
}
