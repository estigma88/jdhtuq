package co.edu.uniquindio.utils.communication.integration.sender;

import co.edu.uniquindio.utils.communication.message.Message;
import org.springframework.context.Lifecycle;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestTimeout = "${communication.integration.default-request-timeout}",
        defaultReplyTimeout = "${communication.integration.default-replay-timeout}")
public interface MessageSender extends Lifecycle {
    Message send(Message request);
}
