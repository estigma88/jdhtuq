package co.edu.uniquindio.utils.communication.transfer.response;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;
import co.edu.uniquindio.utils.communication.transfer.MessageStreamProcessor;
import co.edu.uniquindio.utils.communication.transfer.network.CommunicationManagerTCP;

import java.io.InputStream;
import java.io.OutputStream;

public class MessageStreamProcessorExecution implements MessageStreamProcessorOutput {
    private CommunicationManagerTCP communicationManager;
    private final Observable<MessageStream> observable;

    public MessageStreamProcessorExecution(Observable<MessageStream> observable) {
        this.observable = observable;
    }

    @Override
    public MessageStream process(MessageStream messageStream, OutputStream outputStream) {
        MessageStream response = null;
        if (communicationManager.getMessageStreamProcessor() != null) {
            response = communicationManager.getMessageStreamProcessor().process(messageStream);
        }

        if(response != null){
            communicationManager.send(response, outputStream, (name, current, size) -> {});
            observable.notifyMessage(response);
        }

        return null;
    }

    public void setCommunicationManager(CommunicationManagerTCP communicationManager) {
        this.communicationManager = communicationManager;
    }
}
