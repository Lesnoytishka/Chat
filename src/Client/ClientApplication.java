package Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

import java.util.Arrays;


public class ClientApplication extends Application {

    private Socket socket;
    private String nickName;
    private String login;
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

    private File messageHistory;

    public ClientApplication(String login, String nickName, String ip_address, int port) {
        this.login = login;
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

        historyActivation();
    }

    private void historyActivation(){
        String historyPath = "History\\";
        File historyPath_dir = new File(historyPath);
        messageHistory = new File(historyPath + "history_" + nickName + ".txt");

        if (!historyPath_dir.exists()){
            historyPath_dir.mkdir();
        }

        if (!messageHistory.exists()){
            try {
                messageHistory.createNewFile();
            } catch (IOException e) {
                taChatLog.appendText("Не удалось подключится к файлу-архиву сообщений! " +
                        "\nПопробуйте запустить чат от имени администратора. " +
                        "\nВ случае повторения ошибки ... вините программистов\n");
            }
        }

        getListMessage();

    }

    private void getListMessage(){
        try {
            RandomAccessFile raf = new RandomAccessFile(messageHistory, "r");
            int historyLength = 100;
            int count = 0;
            int historyHaveLength = 0;

            long pointer = messageHistory.length() - 1;

            for (int i = 0; pointer >= 0 && i < historyLength; pointer--){
                raf.seek(pointer);
                int chr = raf.read();
                if (chr == '\n'){
                    i++;
                    historyHaveLength++;
                }
            }

            pointer++;

            while (count < historyHaveLength) {

                raf.seek(pointer);

                String line = new String(raf.readLine().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                taChatLog.appendText(line + "\n");
                pointer = raf.getFilePointer();
                count++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Scene setSceneComponent(){
        try {
            socket = new Socket(ip_address, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("/hello " + login);
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


//        miChangeNickName.setOnAction(event -> {
//            RenamedWindow renamedWindow = new RenamedWindow(this, nickName);
////            closeApp();
//
////              todo реализовать изменение ника без дисконекта
//            stage.setTitle(String.format("Чат. [%s]", nickName));
//            try {
//                out.writeUTF("/setName " + nickName);
//                out.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        });

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
                        if (message.startsWith("/userList ;")) {

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

                            String messageContent = timeLocal + " " + message + "\n";
                            taChatLog.appendText(messageContent);


                            byte[] messageContentBytes = messageContent.getBytes();
                            try (FileOutputStream writeHistory = new FileOutputStream(messageHistory, true)) {
                                writeHistory.write(messageContentBytes);
                                writeHistory.flush();
                            } catch (IOException ex){
                                ex.printStackTrace();
                            }


                        }
                    }
                } catch (IOException ex) {
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
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
