package Client;

import Network.AuthClients;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import test.ClientApp;

import java.io.IOException;

public class RenamedWindow extends Application {
    private TextField tfRename = new TextField();
    private Button btnConfirm = new Button("Переименовать");
    private Button btnCancel = new Button("Отменить");

    private String nickName;
    private ClientApplication clientApp;

    public RenamedWindow(ClientApplication clientApplication, String nickName){
        this.clientApp = clientApplication;
        this.nickName = nickName;
        Stage stage = new Stage();
        try {
            start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox content = new HBox();
        VBox buttonsArea = new VBox(20);

        content.getChildren().addAll(tfRename, buttonsArea);
        buttonsArea.getChildren().addAll(btnConfirm, btnCancel);

        Scene scene = new Scene(content);
        primaryStage.setScene(scene);

        btnCancel.setOnAction(event -> primaryStage.close());

        btnConfirm.setOnAction(event -> {
            String newNickName = tfRename.getText().trim();
            AuthClients authClients = new AuthClients();
            authClients.changeNickName(nickName, newNickName);
            clientApp.setNickName(newNickName);
            primaryStage.close();

//            try {
//                Parent root = FXMLLoader.load(getClass().getResource("FXML/AuthApplication.fxml"));
//                Scene sceneAUTH = new Scene(root);
//                primaryStage.setScene(sceneAUTH);
//                primaryStage.show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        });
        primaryStage.show();

    }
}
