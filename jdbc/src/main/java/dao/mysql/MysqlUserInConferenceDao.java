package dao.mysql;

import dao.UserInConferenceDao;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Supplier;

@AllArgsConstructor
public class MysqlUserInConferenceDao implements UserInConferenceDao {

    private Supplier<Connection> connectionSupplier;

    @SneakyThrows
    @Override
    public boolean isPresent(int userId, int conferenceId) {
        val sql = "SELECT id FROM `user_in_conference` WHERE user_id = " + userId + " AND conference_id = " + conferenceId;
        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}

