package Network;

public interface TCPConnectionListener {

    void sendMessage(TCPConnections connection, String message);

    void sendMessageFromTo(TCPConnections connection, String from, String toUser, String message);

    void isConnectionReady(TCPConnections connection);

    void isConnectionOnline(TCPConnections connection);

    void isDisconnect(TCPConnections connection);

    void isException(TCPConnections connection, Exception ex);

    void changeName(TCPConnections connections, String oldNickName, String newNickNme);

}
