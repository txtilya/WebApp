package dao;

import lombok.SneakyThrows;
import model.Conference;
import model.User;
import model.messages.ConferenceMessage;

import java.sql.Timestamp;
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


    @SneakyThrows
    Collection<User> getUsersByIdOrLogin(User user, String idOrLogin);
    Collection<User> getFriendsRequests(User user);
    Collection<Conference> getConferences(User u);

    @SneakyThrows
    Collection<Integer> getConferenceIdsByUser(User u);

    Collection<Integer> getUsersIdsFromConference(int conferenceId);
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
    int getIdForMessage();
    int createMessageAndAddToConference(User u, ConferenceMessage m);
    int createMessage(int userId, String content, Timestamp timestamp, int conferenceId);
    boolean isUserInConference(int userId, int conferenceId);
    String getConferenceNameById(int conferenceId);
    boolean confirmFriends(int requesterId, int responderId);
    boolean rejectFriends(int requesterId, int responderId);
}
