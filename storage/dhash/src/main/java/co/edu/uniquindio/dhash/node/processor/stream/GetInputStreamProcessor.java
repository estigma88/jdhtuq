package co.edu.uniquindio.dhash.node.processor.stream;

import co.edu.uniquindio.dhash.node.DHashNode;
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

public class GetInputStreamProcessor implements MessageStreamProcessor {
    private static final Logger logger = Logger
            .getLogger(GetInputStreamProcessor.class);

    private final CommunicationManager communicationManager;
    private final ResourceManager resourceManager;
    private final DHashNode dHashNode;
    private final SerializationHandler serializationHandler;

    public GetInputStreamProcessor(CommunicationManager communicationManager, ResourceManager resourceManager, DHashNode dHashNode, SerializationHandler serializationHandler) {
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
                            .getParam(Protocol.GetParams.RESOURCE_KEY
                                    .name()));

            Message resourceTransferResponseMessage = Message.builder()
                    .id(message.getId())
                    .sendType(Message.SendType.RESPONSE)
                    .messageType(Protocol.GET_RESPONSE)
                    .param(Protocol.GetResponseData.RESOURCE.name(), serializationHandler.encode(resource))
                    .address(Address.builder()
                            .destination(message.getAddress().getSource())
                            .source(dHashNode.getName())
                            .build()).build();

            communicationManager.sendTo(resourceTransferResponseMessage, outputStream);
            communicationManager.transfer(resource.getInputStream(), outputStream, resource.getSize(), (name, current, size) -> {});
        } catch (IOException e) {
            logger.error("Error replicating data", e);
        }
    }
}
