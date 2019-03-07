package Network;

//todo избавиться от данного класса и обращаться напрямую к БД?

public class User {

    private int id;
    private String login;
    private String password;
    private String nickName;

    public User(int id, String login, String password, String nickName) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nickName = nickName;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

}
