package dao.mysql;

import dao.UserDao;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import lombok.val;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Supplier;

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
