package co.edu.uniquindio.utils.communication.transfer.response;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import co.edu.uniquindio.utils.communication.transfer.network.CommunicationManagerTCP;

public class MessageProcessorExecution implements MessageProcessor {
    private final ReturnsManager<Message> responseReleaser;
    private CommunicationManagerTCP communicationManager;
    private final Observable<Message> observable;

    public MessageProcessorExecution(ReturnsManager<Message> responseReleaser, Observable<Message> observable) {
        this.responseReleaser = responseReleaser;
        this.observable = observable;
    }

    @Override
    public Message process(Message message) {
        if (!release(message)) {
            Message response = null;
            if (communicationManager.getMessageProcessor() != null) {
                response = communicationManager.getMessageProcessor().process(message);
            }
            if (response != null) {
                communicationManager.send(response);
                observable.notifyMessage(message);
            }
        }
        return null;
    }

    public boolean release(Message message) {
        if (message.getSendType().equals(Message.SendType.RESPONSE)) {
            responseReleaser.releaseWaitingResult(message.getId(),
                    message);

            return true;
        } else {
            return false;
        }
    }

    public void setCommunicationManager(CommunicationManagerTCP communicationManager) {
        this.communicationManager = communicationManager;
    }
}
