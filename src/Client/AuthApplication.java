package Client;

import javafx.application.Application;
import javafx.event.ActionEvent;
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

    @FXML
    public TextField tfLogin;
    public Button btnEnterInChat;
    public Button btnRegistration;
    public CheckBox checkBoxAnotherSetup;
    public HBox hbAnotherSetup;
    public TextField tfIP;
    public TextField tfPort;
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
        ClientApplication clientApp = new ClientApplication(tfLogin.getText(), tfIP.getText(), Integer.valueOf(tfPort.getText()));
        Stage stage = new Stage();
        clientApp.start(stage);

        final Stage stageAuth = (Stage) btnEnterInChat.getScene().getWindow();
        stageAuth.close();
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
    public void changeAnotherSetup(ActionEvent actionEvent) {
        if (editableSetupChat){
            hbAnotherSetup.setDisable(true);
            editableSetupChat = false;
        } else {
            hbAnotherSetup.setDisable(false);
            editableSetupChat = true;
        }
    }
}

