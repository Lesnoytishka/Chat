package Network;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AuthClients {

    private Map<Integer, User> users = new HashMap<>();
    private String nickName;

    public AuthClients(){

        try {

            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src\\UsersAuth.db")){
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

                while (resultSet.next()){
                    int id = resultSet.getInt("id");

                    String login = resultSet.getString("login");
                    String password = resultSet.getString("password");
                    String nickName = resultSet.getString("nickname");

                    User user = new User(id, login, password, nickName);

                    users.put(id, user);
                }
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addUserInDB(String login, String password, String nickName){
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src\\UsersAuth.db")){
                Statement statement = connection.createStatement();

                if (!checkLoginIsFree(login) && !checkNickNameIsFree(nickName)){
                    connection.setAutoCommit(false);
                        statement.executeUpdate("insert into users (login, password, nickname) " +
                                "values ('" + login + "', '" + password + "', '" + nickName + "')"
                        );
                    connection.commit();

                    ResultSet resultSet = statement.executeQuery("select * from users where login = '" + login + "'");
                    int id = resultSet.getInt("id");
                    users.put(id, new User(id, login, password, nickName));
                    return true;
                }
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkLoginIsFree(String login) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src\\UsersAuth.db")) {
            Statement statement = connection.createStatement();
            return !statement.execute("select * from users where login = '" + login + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkNickNameIsFree(String nickName) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src\\UsersAuth.db")) {
            Statement statement = connection.createStatement();
            return !statement.execute("select * from users where nickname = '" + nickName + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean authUser(String login, String password) {
        String pass = null;
        for (Map.Entry entry : users.entrySet()) {
            User user = (User) entry.getValue();
            if (login.equals(user.getLogin())) {
                pass = ((User) entry.getValue()).getPassword();
                nickName = ((User) entry.getValue()).getNickName();
            }
        }
        return pass != null && pass.equals(password);
    }

    public String getNickName() {
        return nickName;
    }

    public boolean changeNickName(String oldNickName, String newNickName){
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src\\UsersAuth.db")) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * from users where nickname = '" + oldNickName + "'");
                int id = resultSet.getInt("id");
                statement.executeUpdate("update users set nickname = '" + newNickName + "' where id = '" + id + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
