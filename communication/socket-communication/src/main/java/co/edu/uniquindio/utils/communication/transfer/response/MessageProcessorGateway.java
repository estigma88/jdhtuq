package co.edu.uniquindio.utils.communication.transfer.response;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;

public class MessageProcessorGateway implements MessageProcessor {
    private final ResponseReleaser responseReleaser;
    private final CommunicationManager communicationManager;
    private final Observable<Message> observable;

    public MessageProcessorGateway(ResponseReleaser responseReleaser, CommunicationManager communicationManager, Observable<Message> observable) {
        this.responseReleaser = responseReleaser;
        this.communicationManager = communicationManager;
        this.observable = observable;
    }

    @Override
    public Message process(Message message) {
        if (!responseReleaser.release(message)) {
            Message response = null;
            if (communicationManager.getMessageProcessor() != null) {
                response = communicationManager.getMessageProcessor().process(message);
            }
            if (response != null) {
                communicationManager.sendMessageUnicast(response);
                observable.notifyMessage(message);
            }
        }
        return null;
    }
}
