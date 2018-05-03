package co.edu.uniquindio.utils.communication.web.restful;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;

public class MessageProcessorWrapper implements MessageProcessor{
    private MessageProcessor messageProcessor;

    @Override
    public Message process(Message request) {
        if(messageProcessor != null){
            return messageProcessor.process(request);
        }
        return Message.builder().build();
    }

    public void updateMessageProcessor(MessageProcessor messageProcessor){
        this.messageProcessor = messageProcessor;
    }
}
