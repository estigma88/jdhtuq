package co.edu.uniquindio.utils.communication.transfer;

import co.edu.uniquindio.utils.communication.message.MessageStream;

import java.io.OutputStream;

public interface StreamCommunicator extends Communicator, ConnectionListener, StreamManager {
    void send(MessageStream messageStream, OutputStream destination, ProgressStatusTransfer progressStatusTransfer);
}
