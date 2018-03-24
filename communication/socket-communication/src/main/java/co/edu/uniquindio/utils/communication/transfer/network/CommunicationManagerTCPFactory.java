package co.edu.uniquindio.utils.communication.transfer.network;

import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommunicationManagerTCPFactory implements CommunicationManagerFactory {
    private final MessageSerialization messageSerialization;
    private final Map<String, Map<String, String>> instancesProperties;
    private final Map<String, String> defaultProperties;

    public CommunicationManagerTCPFactory(MessageSerialization messageSerialization, Map<String, Map<String, String>> instancesProperties) {
        this.messageSerialization = messageSerialization;
        this.instancesProperties = instancesProperties;

        this.defaultProperties = new HashMap<>();
        this.defaultProperties.put("RESPONSE_TIME", "2000");
        this.defaultProperties.put("BUFFER_SIZE_MULTICAST", "1024");
        this.defaultProperties.put("IP_MULTICAST", "224.0.0.2");
        this.defaultProperties.put("PORT_MULTICAST", "2000");
        this.defaultProperties.put("PORT_TCP_RESOURCE", "2004");
        this.defaultProperties.put("PORT_TCP", "2005");
        this.defaultProperties.put("PORT_UDP", "2006");
        this.defaultProperties.put("BUFFER_SIZE_UDP", "1024");
    }

    @Override
    public CommunicationManager newCommunicationManager(String name) {
        CommunicationManagerTCP communication = new CommunicationManagerTCP(messageSerialization);

        communication.setCommunicationProperties(Optional.ofNullable(instancesProperties.get(name)).orElse(defaultProperties));

        communication.init();

        return communication;
    }
}
