package Server.ControllersAndFXMLs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerApp extends Application  {

    @FXML
    public TextArea taChatLog;

    @FXML
    public TextArea taServChatForClient;

    @FXML
    public Button buttonSendMsg;

    @FXML
    public TextField tfSendMessage;

    @FXML
    private Parent root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            root = FXMLLoader.load(getClass().getResource("../ControllersAndFXMLs/ServerApp.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

            init();

            Platform.runLater(() -> sendMessage("213"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void sendMessage(String value) {
        taChatLog.appendText(value + "\n");
    }

    @FXML
    public void init(){

    }

    public void sendMessage(ActionEvent actionEvent) {
        taChatLog.appendText(tfSendMessage.getText() + "\n");
        sendMessage("sss");
    }
}
