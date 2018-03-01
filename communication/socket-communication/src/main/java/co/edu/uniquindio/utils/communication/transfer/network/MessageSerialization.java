package co.edu.uniquindio.utils.communication.transfer.network;

import co.edu.uniquindio.utils.communication.message.Message;

public interface MessageSerialization {
    String encode(Message message);

    Message decode(String message);
}
