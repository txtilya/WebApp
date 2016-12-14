package model;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;

import java.sql.ResultSet;


@Value
@AllArgsConstructor
public class UserInConference {
    private final int id;
    private final int user_id;
    private final int conference_id;

    @SneakyThrows
    public static UserInConference getFrom(ResultSet resultSet) {
        return new UserInConference(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getInt("conference_id"));
    }
}

