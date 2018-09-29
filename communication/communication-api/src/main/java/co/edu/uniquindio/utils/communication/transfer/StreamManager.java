package co.edu.uniquindio.utils.communication.transfer;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamManager {
    MessageStream receive(Message message, ProgressStatusTransfer progressStatusTransfer);

    void send(MessageStream message, ProgressStatusTransfer progressStatusTransfer);

    void sendTo(Message message, OutputStream destination);

    void transfer(InputStream source, OutputStream destination, Long size, ProgressStatusTransfer progressStatusTransfer) throws IOException;
}
