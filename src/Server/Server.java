package Server;

import Network.TCPConnectionListener;
import Network.TCPConnections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements TCPConnectionListener {

    private ExecutorService executorService;
    private List<TCPConnections> realConnections = Collections.synchronizedList(new ArrayList<>());
    private Map<String, TCPConnections> activeConnections = Collections.synchronizedMap(new HashMap<>());

    public Server(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            executorService = Executors.newCachedThreadPool();
            System.out.println("Server ready...");

            executorService.execute(this::mailingUsers);
//                executorService.execute(this::sendSystemMessage);

            while (true) {
                try {
                    TCPConnections realConnect = new TCPConnections(this, serverSocket.accept(), executorService);
                    realConnections.add(realConnect);
//                    System.out.println("new connection is access " + realConnect);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (executorService != null) {
                executorService.shutdown();
            }
        }
    }

    private void sendMessageToAllConnections(String value) {
        if (!value.startsWith("/userList")) {
            System.out.println(value);
        }
        for (Map.Entry<String, TCPConnections> connections : activeConnections.entrySet()) {
            connections.getValue().sendMessage(value);
        }
    }

    public void sendMessageFromTo(String from, String toUser, String message) {

        if (!(from == null || toUser == null || message.equals(""))) {

            activeConnections.get(toUser).sendMessage(String.format("[%s]--> %s", from, message));
            activeConnections.get(from).sendMessage(String.format("<--[%s] %s", toUser, message));
            System.out.println(String.format("[%s]-->[%s] : %s", from, toUser, message));

        } else {

            System.err.println(String.format("[%s]-->[%s] : %s", from, toUser, message));
            activeConnections.get(from).sendMessage(String.format("<--[%s] %s", toUser,
                    "не удалось отправить сообщение: возможно данный получатель вышел")


            );
        }

    }

    private void mailingUsers() {
        executorService.execute(() -> {
            while (true) {
                try {
                    Thread.sleep(3_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                StringBuilder connectionsList = new StringBuilder("/userList ");
                for (Map.Entry<String, TCPConnections> entry : activeConnections.entrySet()) {
                    connectionsList.append(";").append(entry.getKey());
                }
                sendMessageToAllConnections(connectionsList.toString());
            }
        });
    }

//    private void sendSystemMessage(){
//        while (true) {
//            String message = scanner.nextLine();
//            if (message.equals("/real")) {
//                System.out.println(realConnections);
//            } else if (message.equals("/active")) {
//                System.out.println(activeConnections);
//            } else {
    //            for (TCPConnections connections : realConnections) {
    //                connections.sendMessage(message);
    //            }
//            }
//        }
//    }

    @Override
    public void sendMessageFromTo(TCPConnections connection, String from, String toUser, String message) {
        sendMessageFromTo(from, toUser, message);
    }

    @Override
    public void sendMessage(TCPConnections connection, String message) {
        sendMessageToAllConnections(message);
    }

    @Override
    public void isConnectionReady(TCPConnections connection) {
        realConnections.add(connection);
    }

    @Override
    public void isConnectionOnline(TCPConnections connection) {
        activeConnections.put(connection.getNickName(), connection);
        System.out.println("Client is online: " + connection.toString());
        sendMessageToAllConnections("Client connected: " + connection.getNickName());
    }

    @Override
    public void isDisconnect(TCPConnections connection) {
        if (activeConnections.containsKey(connection.getNickName())) {

            System.out.println("Client disconnected: " + connection);
            activeConnections.remove(connection.getNickName());
            sendMessageToAllConnections("Client disconnected: " + connection.getNickName());

        }
        realConnections.remove(connection);
    }

    @Override
    public void isException(TCPConnections connection, Exception ex) {
        System.out.println("TCP Connection exception: " + ex);
    }

    @Override
    public void changeName(TCPConnections connections, String oldNickName, String newNickName) {
        activeConnections.put(newNickName, connections);
        activeConnections.remove(oldNickName);
    }
}
