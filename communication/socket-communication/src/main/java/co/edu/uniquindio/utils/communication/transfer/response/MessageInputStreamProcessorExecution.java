package co.edu.uniquindio.utils.communication.transfer.response;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageInputStreamProcessor;
import co.edu.uniquindio.utils.communication.transfer.network.CommunicationManagerTCP;

import java.io.InputStream;

public class MessageInputStreamProcessorExecution implements MessageInputStreamProcessor {
    private CommunicationManagerTCP communicationManager;

    public void setCommunicationManager(CommunicationManagerTCP communicationManager) {
        this.communicationManager = communicationManager;
    }

    @Override
    public void process(Message request, InputStream inputStream) {
        if (communicationManager.getMessageInputStreamProcessor() != null) {
            communicationManager.getMessageInputStreamProcessor().process(request, inputStream);
        }
    }
}
