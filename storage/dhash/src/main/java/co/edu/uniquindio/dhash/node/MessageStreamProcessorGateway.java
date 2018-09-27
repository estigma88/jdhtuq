package co.edu.uniquindio.dhash.node;

import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageType;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.MessageStreamProcessor;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MessageStreamProcessorGateway implements MessageStreamProcessor {
    private static final Logger logger = Logger
            .getLogger(MessageStreamProcessorGateway.class);

    private final CommunicationManager communicationManager;
    private final ResourceManager resourceManager;
    private final DHashNode dHashNode;
    private final SerializationHandler serializationHandler;
    private final Map<MessageType, MessageStreamProcessor> messageStreamProcessorMap;

    public MessageStreamProcessorGateway(CommunicationManager communicationManager, ResourceManager resourceManager, DHashNode dHashNode, SerializationHandler serializationHandler) {
        this.communicationManager = communicationManager;
        this.resourceManager = resourceManager;
        this.dHashNode = dHashNode;
        this.serializationHandler = serializationHandler;

        this.messageStreamProcessorMap = new HashMap<>();
        this.messageStreamProcessorMap.put(Protocol.PUT, new PutMessageInputStreamProcessor(resourceManager, dHashNode, serializationHandler));
        this.messageStreamProcessorMap.put(Protocol.RESOURCE_TRANSFER, new ResourceTransferMessageInputStreamProcessor(communicationManager, resourceManager, dHashNode, serializationHandler));
    }

    @Override
    public void process(Message message, InputStream inputStream, OutputStream outputStream) {
        logger.debug("Message to: " + dHashNode.getName() + " Message:["
                + message.toString() + "]");
        logger.debug("Node " + dHashNode.getName() + ", arrived message of "
                + message.getMessageType());

        Optional.ofNullable(messageStreamProcessorMap.get(message.getMessageType()))
                .orElseThrow(() -> new IllegalStateException("Message " + message.getMessageType() + " was not found"))
                .process(message, inputStream, outputStream);
    }
}
