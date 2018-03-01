package co.edu.uniquindio.utils.communication.transfer.network;

import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;

public class CommunicationManagerTCPFactory implements CommunicationManagerFactory{
    @Override
    public CommunicationManager newCommunicationManager(String name) {
        return new CommunicationManagerTCP();
    }
}
