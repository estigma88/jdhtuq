package co.edu.uniquindio.utils.communication.transfer.network;

import co.edu.uniquindio.utils.communication.transfer.ConnectionHandler;

import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ConnectionHandlerAsynchronous implements ConnectionHandler {
    private final ConnectionHandler connectionHandler;
    private final ExecutorService executorService;

    public ConnectionHandlerAsynchronous(ConnectionHandler connectionHandler, ExecutorService executorService) {
        this.connectionHandler = connectionHandler;
        this.executorService = executorService;
    }

    @Override
    public void handle(Socket socket) {
        executorService.execute(() -> connectionHandler.handle(socket));
    }
}
