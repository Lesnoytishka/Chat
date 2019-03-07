package Client;

import Network.AuthClients;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Arrays;

public class ClientApplication extends Application {

    private Socket socket;
    private String nickName;
    private String ip_address;
    private int port;

    private DataInputStream in;
    private DataOutputStream out;

    private TextArea taChatLog;
    private TextField tfSendMessage;

    private ObservableList<String> nicksList = FXCollections.observableArrayList();
    private ListView<String> listView = new ListView<>(nicksList);

    private Stage stage;

    private Thread thrReceive;

    public ClientApplication(String nickName, String ip_address, int port) {
        this.nickName = nickName;
        this.ip_address = ip_address;
        this.port = port;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        primaryStage.setTitle("Чат.");

        Scene scene = setSceneComponent();

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> closeApp());
        primaryStage.show();
    }

    private Scene setSceneComponent(){
        try {
            socket = new Socket(ip_address, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("/hello " + nickName);
            out.flush();
            stage.setTitle(String.format("Чат. [%s]", nickName));
            receiveMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        taChatLog = new TextArea();
        taChatLog.setWrapText(true);
        taChatLog.setEditable(false);

        HBox bottomContent = setSendMessageComponents();

        MenuBar menu = setMenuBarComponent();

        BorderPane layout = new BorderPane();
        layout.setTop(menu);
        layout.setCenter(taChatLog);
        listView.setPrefWidth(120);
        layout.setRight(listView);
        layout.setBottom(bottomContent);

        return new Scene(layout);
    }

    private HBox setSendMessageComponents(){
        tfSendMessage = new TextField();
        Button buttonSendMessage = new Button("Отправить");

        tfSendMessage.setPrefWidth(420);

        buttonSendMessage.setOnAction(event -> sendMessage());

        tfSendMessage.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                sendMessage();
            }
        });

        return new HBox(5, tfSendMessage, buttonSendMessage);
    }

    private MenuBar setMenuBarComponent(){
        Menu menu = new Menu("Меню чата");
        Menu help = new Menu("Help");

        MenuItem miClearChatArea = new MenuItem("Очистить лог чата");
        MenuItem miChangeNickName = new MenuItem("Переименовать");
        MenuItem miAbout = new MenuItem("обо мне");
        MenuItem miHelpChat = new MenuItem("возможности чата");

        menu.getItems().addAll(miClearChatArea, miChangeNickName);
        help.getItems().addAll(miHelpChat, miAbout);

        miClearChatArea.setOnAction(event -> taChatLog.clear());


        miChangeNickName.setOnAction(event -> {
            RenamedWindow renamedWindow = new RenamedWindow(this, nickName);
//            closeApp();



//              todo реализовать изменение ника без дисконекта
            stage.setTitle(String.format("Чат. [%s]", nickName));
            try {
                out.writeUTF("/setName " + nickName);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }


        });

        return new MenuBar(menu, help);
    }



//    ------------------------------------------------------------------------------------------------------------------

    private void sendMessage(){
        if (!tfSendMessage.getText().trim().isEmpty()) {
            if (tfSendMessage.getText().startsWith("/w ")){
                try {
                    String message = tfSendMessage.getText().trim();
                    tfSendMessage.setText("");
                    out.writeUTF(message);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    String message = tfSendMessage.getText().trim();
                    tfSendMessage.setText("");
                    out.writeUTF("/message " + message);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized void receiveMessage(){
        String timeLocal = LocalTime.now().toString().substring(0,5);
        Platform.runLater(() -> {
            thrReceive = new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readUTF();
                        if (message.startsWith("/userList ")) {

                            Platform.runLater(() -> {
                                String[] strings;
                                strings = message.split(";");
                                nicksList.removeAll(listView.getItems());
                                nicksList.addAll(Arrays.asList(strings).subList(1, strings.length));
                            });

                        } else if (message.startsWith("/exit ")) {

                                String[] userSplitName;
                                userSplitName = message.split(";");
                                taChatLog.appendText(timeLocal + " Участник покинул чат: " + userSplitName[1]);

                        } else {

                            taChatLog.appendText(timeLocal + " " + message + "\n");

                        }
                    }
                } catch (IOException ex) {
                    System.err.println("Потеряно соединение с сервером\n");
                    taChatLog.appendText("Потеряно соединение с сервером\n");
                    closeApp();
                }
            });
            thrReceive.start();
        });
    }

    void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private void closeApp() {
        stage.close();
        try {
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
