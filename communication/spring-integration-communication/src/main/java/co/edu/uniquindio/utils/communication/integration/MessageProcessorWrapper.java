package co.edu.uniquindio.utils.communication.integration;

import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageType;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;

import java.util.Optional;

public class MessageProcessorWrapper implements MessageProcessor {
    static final MessageType EMPTY_MESSAGE_TYPE = MessageType.builder()
            .name("EMPTY")
            .amountParams(0)
            .build();
    private MessageProcessor messageProcessor;

    @Override
    public Message process(Message request) {
        Message response = null;

        if (messageProcessor != null) {
            response = messageProcessor.process(request);
        }

        return Optional.ofNullable(response).orElse(Message.builder()
                .sendType(Message.SendType.RESPONSE)
                .messageType(EMPTY_MESSAGE_TYPE)
                .address(Address.builder()
                        .source(request.getAddress().getDestination())
                        .destination(request.getAddress().getSource())
                        .build())
                .build());
    }

    public void updateMessageProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }
}
