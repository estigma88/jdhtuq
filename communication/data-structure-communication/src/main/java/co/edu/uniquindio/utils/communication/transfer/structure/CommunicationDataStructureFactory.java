package co.edu.uniquindio.utils.communication.transfer.structure;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;

import java.util.HashMap;

public class CommunicationDataStructureFactory implements CommunicationManagerFactory {
    @Override
    public CommunicationManager newCommunicationManager(String name) {
        Observable<Message> observable = new Observable<>();
        Observable<MessageStream> observableStream = new Observable<>();

        return new CommunicationDataStructure(new HashMap<>(), new HashMap<>(), observable, observableStream);
    }
}
