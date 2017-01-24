package dao.mysql.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Util {

    public static int getResultSetRowCount(ResultSet resultSet) {
        int size = 0;
        try {
            resultSet.last();
            size = resultSet.getRow();
            resultSet.beforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return size;
    }
}
