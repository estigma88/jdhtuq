package co.edu.uniquindio.utils.communication.transfer;

import java.net.Socket;

public interface ConnectionHandler {
    void handle(Socket socket);
}
