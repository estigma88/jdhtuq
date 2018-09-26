package co.edu.uniquindio.utils.communication.transfer.network;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.transfer.ConnectionHandler;
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
        this.defaultProperties.put("TIMEOUT_TCP_CONNECTION", "2000");
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

        MessageProcessorExecution messageProcessorExecution = new MessageProcessorExecution(returnsManager, observable);

        MulticastManagerUDP multicastManager = new MulticastManagerUDP(messageSerialization);
        MessageProcessorGatewayAsynchronous messageProcessorGatewayAsynchronous = new MessageProcessorGatewayAsynchronous(messageProcessorExecution, messageProcessorExecutor);
        MessagesReceiver multicastMessagesReceiver = new MessagesReceiver(multicastManager, messageProcessorGatewayAsynchronous);

        UnicastManagerTCP unicastManager = new UnicastManagerTCP(messageSerialization);
        MessageInputStreamProcessorExecution messageInputStreamProcessorExecution = new MessageInputStreamProcessorExecution();
        ConnectionHandler connectionHandler = new ConnectionMessageProcessorGateway(messageInputStreamProcessorExecution, messageProcessorExecution, messageSerialization);
        ConnectionHandler connectionHandlerAsynchronous = new ConnectionHandlerAsynchronous(connectionHandler, messageProcessorExecutor);
        ConnectionReceiver connectionReceiver = new ConnectionReceiver(unicastManager, connectionHandlerAsynchronous);

        CommunicationManagerTCP communication = new CommunicationManagerTCP(unicastManager, connectionReceiver, multicastManager, multicastMessagesReceiver, messageResponseProcessor, observable, returnsManager, messagesReceiverExecutor);

        messageInputStreamProcessorExecution.setCommunicationManager(communication);
        messageProcessorExecution.setCommunicationManager(communication);

        communication.init(communicationProperties);

        return communication;
    }
}
