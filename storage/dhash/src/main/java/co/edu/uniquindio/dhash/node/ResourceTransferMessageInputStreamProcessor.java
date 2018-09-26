package co.edu.uniquindio.dhash.node;

import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.MessageStreamProcessor;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ResourceTransferMessageInputStreamProcessor implements MessageStreamProcessor {
    private static final Logger logger = Logger
            .getLogger(ResourceTransferMessageInputStreamProcessor.class);

    private final CommunicationManager communicationManager;
    private final ResourceManager resourceManager;
    private final DHashNode dHashNode;
    private final SerializationHandler serializationHandler;

    public ResourceTransferMessageInputStreamProcessor(CommunicationManager communicationManager, ResourceManager resourceManager, DHashNode dHashNode, SerializationHandler serializationHandler) {
        this.communicationManager = communicationManager;
        this.resourceManager = resourceManager;
        this.dHashNode = dHashNode;
        this.serializationHandler = serializationHandler;
    }

    @Override
    public void process(Message message, InputStream inputStream, OutputStream outputStream) {
        try {
            Resource resource = resourceManager.find(
                    message
                            .getParam(Protocol.ResourceTransferParams.RESOURCE_KEY
                                    .name()));

            Message resourceTransferResponseMessage = Message.builder()
                    .sequenceNumber(message.getSequenceNumber())
                    .sendType(Message.SendType.RESPONSE)
                    .messageType(Protocol.RESOURCE_TRANSFER_RESPONSE)
                    .param(Protocol.ResourceTransferResponseData.RESOURCE.name(), serializationHandler.encode(resource))
                    .address(Address.builder()
                            .destination(message.getAddress().getSource())
                            .source(dHashNode.getName())
                            .build()).build();

            communicationManager.sendMessageUnicast(resourceTransferResponseMessage, outputStream);
            communicationManager.sendMessageUnicast(resource.getInputStream(), outputStream);
        } catch (IOException e) {
            logger.error("Error replicating data", e);
        }
    }
}
