package dao.mysql;

import dao.UserDao;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import lombok.val;
import model.User;
import model.messages.ConferenceMessage;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.function.Supplier;

import static dao.mysql.util.Util.getResultSetRowCount;

@Log
@AllArgsConstructor
public class MysqlUserDao implements UserDao {

    private Supplier<Connection> connectionSupplier;

    @SneakyThrows
    @Override
    public Collection<User> getAll() {
        val users = new HashSet<User>();
        val sql = "SELECT id, email, login, password, role FROM `user`";
        try (Connection connection = connectionSupplier.get();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next())
                users.add(User.getFrom(resultSet));
        }
        return users;
    }

    @SneakyThrows
    @Override
    public Collection<User> getFriends(User user) {
        int userId = user.getId();
        val users = new HashSet<User>();
        val sql = "SELECT id, email, login, password, role FROM `user` WHERE id IN (SELECT responder FROM `friends` WHERE requester = '" +
                userId + "' AND confirmation = '1' UNION SELECT requester FROM `friends` WHERE  responder = '" +
                userId + "' AND confirmation = '1')";
        try (Connection connection = connectionSupplier.get();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next())
                users.add(User.getFrom(resultSet));
        }
        return users;
    }

    @SneakyThrows
    @Override
    public boolean isUsersFriends(int id, int id1) {
        val sql = "SELECT * FROM `friends` WHERE (requester = " + id +
                " AND responder = " + id1 +
                " OR requester = " + id1 +
                " AND responder = " + id +
                ") AND confirmation = 1";
        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @SneakyThrows
    @Override
    public boolean isUsersFriendsRequestExist(int id, int id1) {
        val sql = "SELECT * FROM `friends` WHERE (requester = " + id +
                " AND responder = " + id1 +
                " OR requester = " + id1 +
                " AND responder = " + id +
                ")";
        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @SneakyThrows
    @Override
    public void addFriend(int requesterId, int targetId) {
        val sql = "INSERT INTO `friends` (`id`, `requester`, `responder`, `confirmation`) VALUES (NULL, '" +
                requesterId + "', '" + targetId + "', '0');";
        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    @SneakyThrows
    @Override
    public void delFriend(int requesterId, int targetId) {
        val sql = "DELETE FROM `friends` WHERE (requester = " + requesterId +
                " AND responder = " + targetId +
                " OR requester = " + targetId +
                " AND responder = " + requesterId +
                ") AND confirmation = 1";
        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }


    @SneakyThrows
    @Override
    public Optional<User> getByEmail(String email) {
        val sql = "SELECT id, email, login, password, role FROM `user` WHERE email = " + "'" + email + "'";
        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                return Optional.ofNullable(resultSet.next() ?
                        User.getFrom(resultSet) : null);
            }
        }
    }

    @SneakyThrows
    @Override
    public Optional<User> getById(long id) {
        val sql = "SELECT id, email, login, password, role FROM `user` WHERE id = " + id;
        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                return Optional.ofNullable(resultSet.next() ?
                        User.getFrom(resultSet) : null);
            }
        }
    }

    @SneakyThrows
    @Override
    public boolean isUserExist(String login, String email) {
        val sql = "SELECT id, email, login, password, role FROM `user` WHERE login = " +
                "'" + login + "'" +
                " OR email = " + "'" + email + "'";
        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @SneakyThrows
    @Override
    public void addUser(String login, String email, String pass) {
        val sql = "INSERT INTO `webapp`.`user` (`id`, `email`, `login`, `password`, `role`) VALUES (DEFAULT, '" + email +
                "', '" + login + "', '" + pass + "', 'user')";
        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    @SneakyThrows
    @Override
    public int getOrCreateDialogId(int requesterId, int targetId) {
        User u = getById(targetId).get();
        if (!isUserExist(u.getLogin(), u.getEmail())) return 0;
        int dialogId = getDialog(requesterId, targetId);
        if (dialogId == 0) {
            dialogId = createDialogAndAddUsers(requesterId, targetId);
        }
        return dialogId;
    }


    @SneakyThrows
    @Override
    public int getDialog(int requesterId, int targetId) {
        int dialogId;
        val sql = "SELECT conference.id FROM `user_in_conference` first, `user_in_conference` second, `conference`" +
                " WHERE (first.user_id = " + requesterId + " OR first.user_id = " + targetId +
                ") AND (second.user_id = " + targetId + " OR second.user_id = " + requesterId + ") " +
                "AND first.user_id <> second.user_id AND first.conference_id = second.conference_id " +
                "AND first.conference_id = conference.id AND conference.type = 0 GROUP BY conference.id";
        try (Connection connection = connectionSupplier.get();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) dialogId = resultSet.getInt("id");
            else dialogId = 0;
        }
        return dialogId;
    }

    @SneakyThrows
    @Override
    public int createDialogAndAddUsers(int requesterId, int targetId) {
        int dialogId = createDialog();
        addUserToConference(dialogId, requesterId);
        addUserToConference(dialogId, targetId);
        return dialogId;
    }

    @SneakyThrows
    @Override
    public void addUserToConference(int conferenceId, int userId) {
        val sql = "INSERT INTO `user_in_conference` (`id`, `user_id`, `conference_id`) " +
                "VALUES (NULL, '" + userId + "', '" + conferenceId + "');";
        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    @SneakyThrows
    @Override
    public int createDialog() {
        int dialogId = getIdForDialog();
        val sql = "INSERT INTO `conference` (`id`, `type`, `name`) VALUES ('" + dialogId + "', '0', 'default');";
        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
        return dialogId;
    }


    @SneakyThrows
    @Override
    public int getIdForDialog() {
        val sql = "SELECT * FROM `conference`";
        try (Connection connection = connectionSupplier.get();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return getResultSetRowCount(resultSet) + 1;
        }
    }


    @SneakyThrows
    @Override
    public int getIdForMessage() {
        val sql = "SELECT * FROM `user_message`";
        try (Connection connection = connectionSupplier.get();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return getResultSetRowCount(resultSet) + 1;
        }
    }


//    rs.getTimestamp("updated_time")
//    sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());

    @Override
    public int createMessageAndAddToConference(User u, ConferenceMessage m) {
        int messageId = 0;
        if (isUserInConference(u.getId(), Integer.parseInt(m.getConferenceId()))) {
            Timestamp timestamp = new Timestamp(new Date().getTime());
            messageId = createMessage(u.getId(), m.getContent(), timestamp);
        } else return 0;
        return messageId;
    }

    @SneakyThrows
    @Override
    public int createMessage(int userId, String content, Timestamp timestamp) {
        int messageId = getIdForMessage();
        val sql = "INSERT INTO `user_message` (`id`, `user_id`, `content`, `date`) VALUES (NULL, '" +
                userId + "', '" + content + "', '" + timestamp + "');";
        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
        return messageId;
    }

    @SneakyThrows
    @Override
    public boolean isUserInConference(int userId, int conferenceId) {
        val sql = "SELECT * FROM `user_in_conference` WHERE user_id = " + userId + " AND conference_id = " + conferenceId;
        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

//    @SneakyThrows
//    @Override
//    default Optional<Person> isPersonRegistered(String login, String hash) {
//        String sql = "SELECT id, first_name, last_name, permission, dob, email, password, address, telephone " +
//                "FROM Person WHERE email = ? AND password = ?";
//        try (Connection connection = get();
//             PreparedStatement statement = connection.prepareStatement(sql)) {
//            statement.setString(1, login);
//            statement.setString(2, hash);
//            try (ResultSet resultSet = statement.executeQuery()) {
//                return Optional.ofNullable(resultSet.next() ? Person.getFrom(resultSet) : null);
//            }
//        }
//    }
}
