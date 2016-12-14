package listeners;

import com.hegel.core.StringEncryptUtil;
import common.ConnectionPool;
import dao.mysql.MysqlUserDao;
import dao.mysql.MysqlUserInConferenceDao;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.function.Supplier;

import static com.hegel.core.functions.ExceptionalSupplier.toUncheckedSupplier;

@Log
@WebListener
public class Initer implements ServletContextListener {

    public static final String USER_DAO = "userDao";
    public static final String USER_IN_CONFERENCE_DAO = "userInConferenceDao";

    @Resource(name = "jdbc/TestDB")
    private DataSource dataSource;

    @Override
    @SneakyThrows
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        ConnectionPool connectionPool = ConnectionPool.create(toUncheckedSupplier(dataSource::getConnection));

//        context.getRealPath("/WEB-INF/classes/h2.sql")
//        encryptPasswords(connectionPool);

        context.setAttribute(USER_DAO, new MysqlUserDao(connectionPool));
        context.setAttribute(USER_IN_CONFERENCE_DAO, new MysqlUserInConferenceDao(connectionPool));

    }

    @SneakyThrows
    private void encryptPasswords(Supplier<Connection> connectionSupplier) {

        try (Connection connection = connectionSupplier.get();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, password FROM Person");
             Statement statement1 = connection.createStatement()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String password = resultSet.getString("password");
                statement1.addBatch(
                        "UPDATE Person SET password = '" +
                                StringEncryptUtil.encrypt(password) +
                                "' WHERE id = " + id);
            }

            statement1.executeBatch();
        }
    }
}
