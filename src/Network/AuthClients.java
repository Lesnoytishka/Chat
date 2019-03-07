package Network;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AuthClients {

    private Map<Integer, User> users = new HashMap<>();

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

//            FileReader userArrayReader = new FileReader("src\\Server\\UserArray.txt");
//            Scanner scanner_userReader = new Scanner(userArrayReader);
//
//            while (scanner_userReader.hasNext()) {
//                String user = scanner_userReader.nextLine();
//                Pattern pattern = Pattern.compile(":");
//                String[] user_logAndPass = pattern.split(user);
//                String login = user_logAndPass[0];
//                String password = user_logAndPass[1];
//
//                users.put(login, password);
//            }
//
//            scanner_userReader.close();
//            userArrayReader.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUsersNickName(){
        return "";
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
                    System.out.println(users);
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

//    public boolean authUser(String username, String password) {
//        String pass = users.get();
//        return pass != null && pass.equals(password);
//    }
}
