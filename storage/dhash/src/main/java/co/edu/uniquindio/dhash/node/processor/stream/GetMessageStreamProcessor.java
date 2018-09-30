package co.edu.uniquindio.dhash.node.processor.stream;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;
import co.edu.uniquindio.utils.communication.transfer.MessageStreamProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetMessageStreamProcessor implements MessageStreamProcessor {
    private final ResourceManager resourceManager;
    private final DHashNode dHashNode;
    private final SerializationHandler serializationHandler;

    public GetMessageStreamProcessor(ResourceManager resourceManager, DHashNode dHashNode, SerializationHandler serializationHandler) {
        this.resourceManager = resourceManager;
        this.dHashNode = dHashNode;
        this.serializationHandler = serializationHandler;
    }

    @Override
    public MessageStream process(MessageStream messageStream) {
        Message message = messageStream.getMessage();

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

        return MessageStream.builder()
                .message(resourceTransferResponseMessage)
                .inputStream(resource.getInputStream())
                .size(resource.getSize())
                .build();
    }
}
