package Server;

import Network.TCPConnectionListener;
import Network.TCPConnections;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class Server implements TCPConnectionListener {

    private List<TCPConnections> realConnections = Collections.synchronizedList(new ArrayList<>());
    private Map<String, TCPConnections> activeConnections = Collections.synchronizedMap(new HashMap<>());
    private Scanner scanner = new Scanner(System.in);

    public Server(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server ready...");
            while (true) {
                try {
                    TCPConnections realConnect = new TCPConnections(this, serverSocket.accept());
                    realConnections.add(realConnect);
//                    System.out.println("new connection is access " + realConnect);
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                sendSystemMessage();

            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void sendMessageToAllConnections(String value){
        System.out.println(value);
        for (Map.Entry<String, TCPConnections> connections : activeConnections.entrySet()) {
            connections.getValue().sendMessage(value);
        }
    }

    public void sendMessageFromTo(String from, String toUser, String message) {

        if ( !(from == null || toUser == null || message.equals("")) ) {

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

//    private void sendSystemMessage(){
//        new Thread(() -> {
//            while (true) {
//                String message = scanner.nextLine();
//                if (message.equals("/real")) {
//                    System.out.println(realConnections);
//                } else if (message.equals("/active")) {
//                    System.out.println(activeConnections);
//                }
//                for (TCPConnections connections : realConnections) {
//                    connections.sendMessage(message);
//                }
//            }
//        }).start();
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
            sendMessageToAllConnections("Client disconnected: " + connection);

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
