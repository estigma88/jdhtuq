package co.edu.uniquindio.utils.communication.transfer;

import co.edu.uniquindio.utils.communication.message.Message;

public interface MessageProcessor {
    Message process(Message request);
}
