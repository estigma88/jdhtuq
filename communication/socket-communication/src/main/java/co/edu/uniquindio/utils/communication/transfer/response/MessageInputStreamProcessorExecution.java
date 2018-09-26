package co.edu.uniquindio.utils.communication.transfer.response;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageStreamProcessor;
import co.edu.uniquindio.utils.communication.transfer.network.CommunicationManagerTCP;

import java.io.InputStream;
import java.io.OutputStream;

public class MessageInputStreamProcessorExecution implements MessageStreamProcessor {
    private CommunicationManagerTCP communicationManager;

    @Override
    public void process(Message request, InputStream inputStream, OutputStream outputStream) {
        if (communicationManager.getMessageStreamProcessor() != null) {
            communicationManager.getMessageStreamProcessor().process(request, inputStream, outputStream);
        }
    }

    public void setCommunicationManager(CommunicationManagerTCP communicationManager) {
        this.communicationManager = communicationManager;
    }
}
