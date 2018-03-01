package co.edu.uniquindio.utils.communication.transfer.network;

import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;

public class CommunicationManagerTCPFactory implements CommunicationManagerFactory{
    private final MessageSerialization messageSerialization;

    public CommunicationManagerTCPFactory(MessageSerialization messageSerialization) {
        this.messageSerialization = messageSerialization;
    }

    @Override
    public CommunicationManager newCommunicationManager(String name) {
        return new CommunicationManagerTCP(messageSerialization);
    }
}
