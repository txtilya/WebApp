package model;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;

import java.sql.ResultSet;

@Value
@AllArgsConstructor
public class User {
    private final int id;
    private final String email;
    private final String login;
    private final String password;
    private final String role;

    @SneakyThrows
    public static User getFrom(ResultSet resultSet) {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("password"),
                resultSet.getString("role"));
    }
}
