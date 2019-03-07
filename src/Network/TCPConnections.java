package Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPConnections {

    private String nickName;

    private Socket socket;
    private TCPConnectionListener connectionListener;

    private DataInputStream in;
    private DataOutputStream out;

    private Thread thrListener;

    private PatternsAndFonts patternsHandler = new PatternsAndFonts();

//todo удалить никнейм из конструктора и дать возможность клиенту подключиться (войти в онлайн)
    public TCPConnections (TCPConnectionListener eventListener, String ipAddress, int port) throws IOException{
        this(eventListener, new Socket(ipAddress, port));
    }

    public TCPConnections(TCPConnectionListener connectionListener, Socket socket) throws IOException {
        this.connectionListener = connectionListener;
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        runThreadListner();
    }

    private void runThreadListner() {
        thrListener = new Thread( () -> {
            try {
                TCPConnections thisConnect = TCPConnections.this;

                connectionListener.isConnectionReady(TCPConnections.this);

                while (!thrListener.isInterrupted()) {

                    String message = in.readUTF();
                    sendMessage(message);
                    String[] messageHandler = patternsHandler.useMessageCommandPattern(message);
                    String messageType = messageHandler[0];
                    String messageContent = messageHandler[1];

                    if (!message.equals("")){

                        switch (messageType) {
                            case "/hello":
                                nickName = messageContent;
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
        thrListener.start();
    }

//    --------------------------------------------------------------------------
//    Sender operations

    public synchronized void sendMessage (String value) {
        try {
            out.writeUTF(value/* + "\r\n"*/);
            out.flush();
        } catch (IOException e) {
            connectionListener.isException(TCPConnections.this, e);
            disconnect();
        }
    }

    private void disconnect() {
        thrListener.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            connectionListener.isException(TCPConnections.this, e);
        }
    }

//    --------------------------------------------------------------------------
//    NickName operations

    private void setNickName(String nickName) {
        this.nickName = nickName;
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

    public Socket getSocket() {
        return socket;
    }
}
