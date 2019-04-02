package test;

import Network.TCPConnectionListener;
import Network.TCPConnections;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Arrays;

public class ClientApp extends Application implements TCPConnectionListener {

    public TextField tfSendMsgContent;
    public Button btnSendMsg;
    public TextField tfSendMsgToUser;
    public TextArea taChatArea;

    private Socket socket;
    private TCPConnections connection;

    private DataInputStream in;
    private DataOutputStream out;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("ClientApp.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);

        primaryStage.setTitle("hello");

        primaryStage.show();

       /* try {
            connection = new TCPConnections(this, "localhost", 8888);
            socket = connection.getSocket();
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("/hello stepan");
            out.writeUTF("/message hi all");
            out.flush();

            System.out.println("connection is ready");
        } catch (IOException e) {
            taChatArea.appendText("Connected exception: " + e);
        }
        startListenerThread();*/
    }

    private void startListenerThread(){
        String timeLocal = LocalTime.now().toString().substring(0,5);
        Thread thrListener = new Thread(() -> {

            try {
                in = new DataInputStream(socket.getInputStream());
                while (!Thread.currentThread().isInterrupted()) {
                    String message = in.readUTF();
//                    taChatArea.appendText(timeLocal + " " + message + "\n);
                    System.out.println(message);
//                    if (message.startsWith("/userList ")) {
//
//                        Platform.runLater(() -> {
//                            String[] strings;
//                            strings = message.split(";");
//                            nicksList.removeAll(listView.getItems());
//                            nicksList.addAll(Arrays.asList(strings).subList(1, strings.length));
//                        });
//
////                    } else if (message.startsWith("/exit ")) {
////
////                        String[] strings;
////                        strings = message.split(";");
////                        taChatArea.appendText(timeLocal + " Участник покинул чат: " + strings[1]);
////
//                    } else {
//
//                        taChatArea.appendText(timeLocal + " " + message + "\n");
////
//                    }
                }
            } catch (IOException ex) {
                System.err.println("Потеряно соединение с сервером\n");
                taChatArea.appendText("Потеряно соединение с сервером\n");

            }
        });
        thrListener.start();
    }

//    ------------------------------------------------------------------------------------------------------------------
//    Sender methods

    public void sendMessage() {
        Platform.runLater(() ->  {
            while (true) {
                String message = tfSendMsgContent.getText().trim();
                System.out.println(message);
                if (!message.equals("")) {
                    tfSendMsgContent.setText("");
                    try {
                        System.out.println(message);
                        if (tfSendMsgToUser.getText().trim().equals("")) {
                            out.writeUTF(message);
                            out.flush();
                        }
                    } catch (IOException e) {
                        System.out.println("не удалось отправить сообщение");
                    }
                }
            }
        });

    }

    public void sendMessageKey(KeyEvent keyEvent) throws IOException {

        if (keyEvent.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }

//    ------------------------------------------------------------------------------------------------------------------

    @Override
    public void sendMessage(TCPConnections connection, String message) {
        while (true){
            taChatArea.setText("123\n");
        }
    }

    @Override
    public void sendMessageFromTo(TCPConnections connection, String from, String toUser, String message) {

    }

    @Override
    public void isConnectionReady(TCPConnections connection) {

    }

    @Override
    public void isConnectionOnline(TCPConnections connection) {

    }

    @Override
    public void isDisconnect(TCPConnections connection) {

    }

    @Override
    public void isException(TCPConnections connection, Exception ex) {

    }

    @Override
    public void changeName(TCPConnections connections, String oldNickName, String newNickNme) {

    }
}
