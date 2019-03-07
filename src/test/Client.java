package test;

import Network.TCPConnectionListener;
import Network.TCPConnections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client implements TCPConnectionListener {

    private Socket socket;
    private String nickName = "Dima";
    private DataInputStream in;
    private DataOutputStream out;
    Scanner scanner = new Scanner(System.in);
//    private TCPConnections connections = new TCPConnections(this, ;

    public static void main(String[] args) throws IOException {
        new Client();
    }

    Client() throws IOException {
        try {

            socket = new Socket("localhost", 8888);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("/hello " + nickName);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread thr = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        String messageIn = in.readUTF();
                        System.out.println(messageIn);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thr.start();

        new Thread(new Runnable() {
            @Override
            public void run() {

                        while (true){
                            try {
                                String messageOut = scanner.nextLine();
                                out.writeUTF(messageOut);
                                out.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

            }
        }).start();

    }

    @Override
    public void sendMessage(TCPConnections connection, String message) {
        System.out.println("1 " + message);
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
}