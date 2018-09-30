package co.edu.uniquindio.utils.communication.transfer.network;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;
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
        this.defaultProperties.put("response_time", "2000");
        this.defaultProperties.put("buffer_size_multicast", "1024");
        this.defaultProperties.put("ip_multicast", "224.0.0.2");
        this.defaultProperties.put("port_multicast", "2000");
        this.defaultProperties.put("timeout_tcp_connection", "2000");
        this.defaultProperties.put("port_tcp", "2005");
        this.defaultProperties.put("size_tcp_buffer", "1024");
    }

    @Override
    public CommunicationManager newCommunicationManager(String name) {
        Map<String, String> communicationProperties = Optional.ofNullable(instancesProperties.get(name)).orElse(defaultProperties);

        ExecutorService messageProcessorExecutor = Executors.newCachedThreadPool();
        ExecutorService messagesReceiverExecutor = Executors.newFixedThreadPool(2);

        ReturnsManager<Message> returnsManager = new ReturnsManagerCommunication<>();
        Observable<Message> observable = new Observable<>();
        Observable<MessageStream> streamObservable = new Observable<>();

        MessageProcessorExecution messageProcessorExecution = new MessageProcessorExecution(returnsManager, observable);

        MulticastManagerUDP multicastManager = new MulticastManagerUDP(messageSerialization);
        MessageProcessorGatewayAsynchronous messageProcessorGatewayAsynchronous = new MessageProcessorGatewayAsynchronous(messageProcessorExecution, messageProcessorExecutor);
        MessagesReceiver multicastMessagesReceiver = new MessagesReceiver(multicastManager, messageProcessorGatewayAsynchronous);

        UnicastManagerTCP unicastManager = new UnicastManagerTCP(messageSerialization);
        MessageStreamProcessorExecution messageInputStreamProcessorExecution = new MessageStreamProcessorExecution(streamObservable);
        ConnectionHandler connectionHandler = new ConnectionMessageProcessorGateway(messageInputStreamProcessorExecution, messageProcessorExecution, messageSerialization);
        ConnectionHandler connectionHandlerAsynchronous = new ConnectionHandlerAsynchronous(connectionHandler, messageProcessorExecutor);
        ConnectionReceiver connectionReceiver = new ConnectionReceiver(unicastManager, connectionHandlerAsynchronous);

        CommunicationManagerTCP communication = new CommunicationManagerTCP(unicastManager, connectionReceiver, multicastManager, multicastMessagesReceiver, messageResponseProcessor, observable, streamObservable, returnsManager, messagesReceiverExecutor);

        messageInputStreamProcessorExecution.setCommunicationManager(communication);
        messageProcessorExecution.setCommunicationManager(communication);

        communication.init(communicationProperties);

        return communication;
    }
}
