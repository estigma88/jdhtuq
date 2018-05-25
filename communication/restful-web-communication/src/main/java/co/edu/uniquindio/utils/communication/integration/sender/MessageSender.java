package co.edu.uniquindio.utils.communication.integration.sender;

import co.edu.uniquindio.utils.communication.message.Message;
import org.springframework.context.Lifecycle;

public interface MessageSender extends Lifecycle {
    Message send(Message request);
}
