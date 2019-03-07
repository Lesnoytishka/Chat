package Client;

import Network.AuthClients;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClientRegistrationApp extends Application {

    @FXML
    public TextField tfLogin;
    @FXML
    public TextField tfNickName;
    @FXML
    public TextField tfPassword;
    @FXML
    public TextField tfConfirmPassword;
    @FXML
    public Button btnCreateUser;
    @FXML
    public Button btnCancel;

    private AuthClients auth = new AuthClients();

    //todo реазвать проверку доступноти логина и никнейма при потере полем фокуса
    // если имя (логин или никнейм) доступно - подсвечивать зеленым, если нет - красным.

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/ClientRegistrationApp.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    public void createUser() throws Exception {
        if (auth.addUserInDB(tfLogin.getText().trim(), tfPassword.getText().trim(), tfNickName.getText().trim())){
            closeRegistrationWindow();
        }
    }

    @FXML
    public void cancelRegistration() throws Exception {
        closeRegistrationWindow();
    }

    private void closeRegistrationWindow()throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("FXML/AuthApplication.fxml"));
        Scene scene = new Scene(root);
        Stage stageRegistration = new Stage();
        stageRegistration.setScene(scene);
        AuthApplication authApp = new AuthApplication();
        authApp.start(stageRegistration);

        final Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
