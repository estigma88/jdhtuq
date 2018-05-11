package co.edu.uniquindio.utils.communication.web.restful;

import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class Trans2 implements Transformer {
    @Override
    public Message<?> transform(Message<?> message) {
        ExtendedMessage extendedMessage = (ExtendedMessage) message.getPayload();

        return MessageBuilder
                .withPayload(ExtendedMessage.builder()
                        .data((co.edu.uniquindio.utils.communication.message.Message) message.getPayload())
                        .replyChannelId((String) message.getHeaders().get("replyChannel"))
                        .errorChannelId((String) message.getHeaders().get("replyChannel"))
                        .build())
                .copyHeaders(message.getHeaders())
                .build();
    }
}
