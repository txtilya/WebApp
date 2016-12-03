package dao.mysql;

import dao.UserDao;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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
