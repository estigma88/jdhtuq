package co.edu.uniquindio.utils.communication.transfer.network;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.transfer.Communicator;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import co.edu.uniquindio.utils.communication.transfer.response.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommunicationManagerTCPFactory implements CommunicationManagerFactory {
    private final MessageSerialization messageSerialization;
    private final MessageResponseProcessor messageResponseProcessor;
    private final Map<String, Map<String, String>> instancesProperties;
    private final Map<String, String> defaultProperties;

    public CommunicationManagerTCPFactory(MessageSerialization messageSerialization, MessageResponseProcessor messageResponseProcessor, Map<String, Map<String, String>> instancesProperties) {
        this.messageSerialization = messageSerialization;
        this.messageResponseProcessor = messageResponseProcessor;
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
        Map<String, String> communicationProperties = Optional.ofNullable(instancesProperties.get(name)).orElse(defaultProperties);

        ExecutorService messageProcessorExecutor = Executors.newCachedThreadPool();
        ExecutorService messagesReceiverExecutor = Executors.newFixedThreadPool(2);

        ReturnsManager<Message> returnsManager = new ReturnsManagerCommunication<>();
        Observable<Message> observable = new Observable<>();

        MessageProcessorGateway messageProcessorGateway = new MessageProcessorGateway(returnsManager, observable);
        MessageProcessor messageProcessorGatewayAsynchronous = new MessageProcessorGatewayAsynchronous(messageProcessorGateway, messageProcessorExecutor);

        Communicator multicastManager = new MulticastManagerUDP(messageSerialization);
        Communicator unicastManager = new UnicastManagerTCP(messageSerialization);
        MessagesReceiver unicastMessagesReceiver = new MessagesReceiver(unicastManager, messageProcessorGatewayAsynchronous);
        MessagesReceiver multicastMessagesReceiver = new MessagesReceiver(multicastManager, messageProcessorGatewayAsynchronous);

        CommunicationManagerTCP communication = new CommunicationManagerTCP(unicastManager, unicastMessagesReceiver, multicastManager, multicastMessagesReceiver, messageResponseProcessor, observable, returnsManager, messagesReceiverExecutor);

        messageProcessorGateway.setCommunicationManager(communication);

        communication.init(communicationProperties);

        return communication;
    }
}
