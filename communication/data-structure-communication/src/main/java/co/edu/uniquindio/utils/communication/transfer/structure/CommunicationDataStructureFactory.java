package co.edu.uniquindio.utils.communication.transfer.structure;

import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;

public class CommunicationDataStructureFactory implements CommunicationManagerFactory {
    @Override
    public CommunicationManager newCommunicationManager(String name) {
        return new CommunicationDataStructure();
    }
}
