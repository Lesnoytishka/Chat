package Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class TCPConnections {

    private String nickName;
    private String login;

    private Socket socket;
    private TCPConnectionListener connectionListener;

    private DataInputStream in;
    private DataOutputStream out;

    private ExecutorService executorService;

    private PatternsAndFonts patternsHandler = new PatternsAndFonts();

//todo удалить никнейм из конструктора и дать возможность клиенту подключиться (войти в онлайн)
    public TCPConnections (TCPConnectionListener eventListener, String ipAddress, int port, ExecutorService executorService) throws IOException{
        this(eventListener, new Socket(ipAddress, port), executorService);
    }

    public TCPConnections(TCPConnectionListener connectionListener, Socket socket, ExecutorService executorService) throws IOException {
        this.executorService = executorService;
        this.connectionListener = connectionListener;
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        runThreadListener();
    }

    private void runThreadListener() {
        executorService.execute(() -> {
            try {
                TCPConnections thisConnect = TCPConnections.this;

                connectionListener.isConnectionReady(TCPConnections.this);

                while (!Thread.currentThread().isInterrupted()) {

                    String message = in.readUTF();
                    String[] messageHandler = patternsHandler.useMessageCommandPattern(message);
                    String messageType = messageHandler[0];
                    String messageContent = messageHandler[1];

                    if (!message.equals("")){

                        switch (messageType) {
                            case "/hello":
                                nickName = AuthClients.getName(messageContent);
                                connectionListener.isConnectionOnline(thisConnect);
                                break;

                            case "/message":
                                connectionListener.sendMessage(thisConnect, String.format("[%s] %s", nickName, messageContent));
                                break;

                            case "/w":
                                String[] content = patternsHandler.useFromToMessagePattern(messageContent);
                                String recipient = content[0];
                                String messageText = content[1];

                                connectionListener.sendMessageFromTo(
                                        thisConnect, nickName, recipient, messageText
                                );
                                break;

                            case "/setName":
                                setNickName(messageContent);
                                break;

                            default:
                                connectionListener.sendMessageFromTo(
                                        thisConnect, nickName, nickName,
                                        "ERROR: Попытка отправить неизвестную команду: " + message
                                );
                        }
                    }
                }
            } catch (IOException e) {
                connectionListener.isException(TCPConnections.this, e);
            } finally {
                connectionListener.isDisconnect(TCPConnections.this);
            }
        });
    }

//    --------------------------------------------------------------------------
//    Sender operations

    public synchronized void sendMessage (String value) {
        try {
            out.writeUTF(value);
            out.flush();
        } catch (IOException e) {
            connectionListener.isException(TCPConnections.this, e);
            disconnect();
        }
    }

    private void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            connectionListener.isException(TCPConnections.this, e);
        }
    }

//    --------------------------------------------------------------------------
//    NickName operations

    private void setNickName(String nickName) {
        String oldNickName = this.nickName;
        this.nickName = nickName;
        connectionListener.changeName(TCPConnections.this, oldNickName, nickName);
    }

    public String getNickName() {
        return nickName;
    }

    @Override
    public String toString() {
        return String.format(
                "[%s] || ip: [%s] || port: [%s]",
                nickName, socket.getInetAddress(), socket.getPort()
        );
    }

    public String getLogin() {
        return login;
    }
}
