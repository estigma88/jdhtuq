package co.edu.uniquindio.utils.communication.transfer;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;

public interface StreamManager {
    MessageStream receive(Message message, ProgressStatusTransfer progressStatusTransfer);

    Message send(MessageStream messageStream, ProgressStatusTransfer progressStatusTransfer);
}
