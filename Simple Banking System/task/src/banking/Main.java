package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        Connection connection = null;
        String url = "jdbc:sqlite:" + args[1];
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try {
            connection = dataSource.getConnection();
      //  try (Connection con = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("Create Table IF NOT EXISTS card(" +
                            "id INTEGER, " +
                            "number TEXT, " +
                            "pin TEXT, " +
                            "balance INTEGER DEFAULT 0)");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Session session = new Session(connection);
        boolean logged = false;

        while (true) {
            if (!logged) {
                logged = session.startSession();
            } else {
                logged = session.userLogged();
            }
        }
    }
}
