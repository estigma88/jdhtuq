package co.edu.uniquindio.utils.communication.web.restful;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;

import java.util.Optional;

public class MessageProcessorWrapper implements MessageProcessor {
    private MessageProcessor messageProcessor;

    @Override
    public Message process(Message request) {
        Message response = null;

        if (messageProcessor != null) {
            response = messageProcessor.process(request);
        }

        return Optional.ofNullable(response).orElse(Message.builder().build());
    }

    public void updateMessageProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }
}
