package co.edu.uniquindio.utils.communication.transfer;

import java.io.IOException;
import java.net.Socket;

public interface ConnectionListener {
    Socket listen() throws IOException;
}
