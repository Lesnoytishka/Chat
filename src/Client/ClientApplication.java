package Client;

import Network.AuthClients;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private boolean authorizationIsOK = false;

    private DataInputStream in;
    private DataOutputStream out;

    private TextArea taChatLog;
    private TextField tfSendMessage;

    private ObservableList<String> nicksList = FXCollections.observableArrayList();
    private ListView<String> listView = new ListView<>(nicksList);

    private Scene scene;
    private Stage stage;

    private Thread thrReceive;

    private AuthClients authClients = new AuthClients();
//    private PatternsAndFont patterns = new PatternsAndFont();

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

        scene = setSceneComponent();

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> closeApp());
        primaryStage.show();
    }

//    ------------------------------------------------------------------------------------------------------------------

//    private Scene setAuthSceneComponent() {
//
//        Label labelWelcome = new Label();
//            labelWelcome.setText("Добро пожаловать! Пожалуйста, авторизируйтесь");
//
//        Label labelLogin = new Label();
//            labelLogin.setText("Логин");
//
//        Label labelPassword = new Label();
//            labelPassword.setText("Пароль");
//
//        Label labelIpAddress = new Label();
//            labelIpAddress.setText("IP_ADDRESS");
//
//        Label labelPort = new Label();
//            labelPort.setText("Порт сервера");
//
//        TextField tfLogin = new TextField();
//            tfLogin.setAlignment(Pos.CENTER);
//
//        TextField tfPassword = new TextField();
//            tfPassword.setAlignment(Pos.CENTER);
//
//        TextField tfIpAddress = new TextField("localhost");
//            tfIpAddress.setAlignment(Pos.CENTER);
//            tfIpAddress.setEditable(false);
//
//        tfIpAddress.setDisable(true);
//
//        TextField tfPort = new TextField("8888");
//            tfPort.setAlignment(Pos.CENTER);
//            tfPort.setEditable(false);
//
//        tfPort.setDisable(true);
//
//        CheckBox cbAnotherSetup = new CheckBox();
//           cbAnotherSetup.setDisable(true);
//
//        Button buttonCheck = new Button("Выбрать сервер вручную");
//
//
//        buttonCheck.setOnAction(event -> {
//            cbAnotherSetup.setSelected(!cbAnotherSetup.isSelected());
//
//            if (cbAnotherSetup.isSelected()) {
//
//                tfIpAddress.setEditable(true);
//                tfPort.setEditable(true);
//
//                tfIpAddress.setDisable(false);
//                tfPort.setDisable(false);
//
//                tfIpAddress.setText(ipAddress_temp);
//                tfPort.setText(port_temp);
//
//            } else {
//
//                ipAddress_temp = tfIpAddress.getText();
//                port_temp = tfPort.getText();
//
//                tfIpAddress.setEditable(false);
//                tfPort.setEditable(false);
//
//                tfIpAddress.setDisable(true);
//                tfPort.setDisable(true);
//
//                tfIpAddress.setText("localhost");
//                tfPort.setText("8888");
//
//            }
//        });
//
//        Button confirmCreateChat = new Button("Ввойти в чат");
//
//        confirmCreateChat.setOnAction( event -> {
//
//                nickName = tfLogin.getText().trim();
//                String password = tfPassword.getText().trim();
//                ip_address = tfIpAddress.getText().trim();
//                port = Integer.valueOf(tfPort.getText().trim());
//
//                if (authClients.authUser(nickName, password)) {
//                    scene = setSceneComponent();
//                    stage.setScene(scene);
//                    authorizationIsOK = true;
//                    receiveMessage();
//                }
//
//        });
//
//        HBox layoutCheck = new HBox(cbAnotherSetup, buttonCheck);
//        layoutCheck.setAlignment(Pos.CENTER);
//
//        VBox layout = new VBox(10);
//
//        layout.setPrefSize(400,400);
//
//        layout.setAlignment(Pos.CENTER);
//        layout.getChildren().addAll(
//                labelWelcome, labelLogin, tfLogin, labelPassword, tfPassword,
//                layoutCheck, labelIpAddress, tfIpAddress, labelPort, tfPort,
//                confirmCreateChat
//        );
//
//        return new Scene(layout);
//    }

//    ------------------------------------------------------------------------------------------------------------------

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

        HBox bottomContent =setSendMessageComponents();

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

        MenuItem clearChatArea = new MenuItem("Очистить лог чата");
        MenuItem about = new MenuItem("обо мне");
        MenuItem helpChat = new MenuItem("возможности чата");

        menu.getItems().add(clearChatArea);
        help.getItems().addAll(helpChat, about);

        clearChatArea.setOnAction(event -> taChatLog.clear());

        return new MenuBar(menu, help);
    }

//    ------------------------------------------------------------------------------------------------------------------

    private void sendMessage(){
        if (!tfSendMessage.getText().trim().isEmpty()) {
            try {
                String message = tfSendMessage.getText().trim();
                tfSendMessage.setText("");
                out.writeUTF(message);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void receiveMessage(){
        String timeLocal = LocalTime.now().toString().substring(0,5);
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

                            String[] strings;
                            strings = message.split(";");
                            taChatLog.appendText(timeLocal + " Участник покинул чат: " + strings[1]);

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
    }

    private void closeApp() {
        if (authorizationIsOK) {
            try {
                thrReceive.interrupt();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
