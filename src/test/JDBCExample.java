package test;

import java.sql.*;

public class JDBCExample {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src\\UseAuth.db")){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

            while (resultSet.next()){
                String login = resultSet.getString("login");
                System.out.println(login);
            }
        }
    }


}
