package co.edu.uniquindio.utils.communication.transfer.structure;

import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommunicationDataStructureFactory implements CommunicationManagerFactory {
    @Override
    public CommunicationManager newCommunicationManager(String name) {
        CommunicationManagerStructure communication = new CommunicationManagerStructure();

        Map<String, String> params = new HashMap<>();
        params.put("RESPONSE_TIME", "2000");

        communication
                .setCommunicationProperties(params);

        communication.init();

        return communication;
    }
}
