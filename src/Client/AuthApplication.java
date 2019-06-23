package Client;

import Network.AuthClients;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AuthApplication extends Application {

    private boolean editableSetupChat = false;
    private AuthClients authSetup = new AuthClients();

    @FXML
    public TextField tfLogin;
    @FXML
    public Button btnEnterInChat;
    @FXML
    public Button btnRegistration;
    @FXML
    public CheckBox checkBoxAnotherSetup;
    @FXML
    public HBox hbAnotherSetup;
    @FXML
    public TextField tfIP;
    @FXML
    public TextField tfPort;
    @FXML
    public PasswordField pfPassword;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/AuthApplication.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    public void runClientApplication() {
        if (authSetup.authUser(tfLogin.getText(), pfPassword.getText())) {

            ClientApplication clientApp = new ClientApplication(tfLogin.getText(), authSetup.getNickName(tfLogin.getText()), tfIP.getText(), Integer.valueOf(tfPort.getText()));
            Stage stage = new Stage();
            clientApp.start(stage);

            final Stage stageAuth = (Stage) btnEnterInChat.getScene().getWindow();
            stageAuth.close();
        }
    }

    @FXML
    public void runRegistrationApplication() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXML/ClientRegistrationApp.fxml"));
            Scene scene = new Scene(root);
            Stage stageRegistration = new Stage();
            stageRegistration.setScene(scene);
            ClientRegistrationApp registrationApp = new ClientRegistrationApp();
            registrationApp.start(stageRegistration);

            final Stage stage = (Stage) btnRegistration.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void changeAnotherSetup() {
        if (editableSetupChat){
            hbAnotherSetup.setDisable(true);
            editableSetupChat = false;
        } else {
            hbAnotherSetup.setDisable(false);
            editableSetupChat = true;
        }
    }
}

