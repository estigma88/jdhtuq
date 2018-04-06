package co.edu.uniquindio.dht.it.socket.node;

import co.edu.uniquindio.dhash.resource.BytesResource;
import co.edu.uniquindio.dht.it.socket.Protocol;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import org.apache.log4j.Logger;

public class NodeMessageProcessor implements MessageProcessor {
    private static final Logger logger = Logger
            .getLogger(NodeMessageProcessor.class);

    private final StorageNode storageNode;

    public NodeMessageProcessor(StorageNode storageNode) {
        this.storageNode = storageNode;
    }

    @Override
    public Message process(Message request) {
        Message response = null;

        if (request.getMessageType().equals(Protocol.GET)) {
            response = processGet(request);
        }
        if (request.getMessageType().equals(Protocol.PUT)) {
            response = processPut(request);
        }

        return response;
    }

    private Message processPut(Message request) {
        BytesResource resource = new BytesResource(request.getParam(Protocol.PutParams.RESOURCE_NAME.name()), request.getData(Protocol.PutDatas.RESOURCE.name()));

        try {
            storageNode.put(resource);
        } catch (StorageException e) {
            logger.error("Problem doing put", e);
            return Message.builder()
                    .sendType(Message.SendType.RESPONSE)
                    .messageType(Protocol.PUT_RESPONSE)
                    .param(Protocol.PutResponseParams.MESSAGE.name(), e.getMessage())
                    .build();
        }

        return Message.builder()
                .sendType(Message.SendType.RESPONSE)
                .messageType(Protocol.PUT_RESPONSE)
                .param(Protocol.PutResponseParams.MESSAGE.name(), "OK")
                .build();
    }

    private Message processGet(Message request) {
        try {
            BytesResource resource = (BytesResource) storageNode.get(request.getParam(Protocol.GetParams.RESOURCE_NAME.name()));

            return Message.builder()
                    .sendType(Message.SendType.RESPONSE)
                    .messageType(Protocol.GET_RESPONSE)
                    .param(Protocol.GetResponseParams.MESSAGE.name(), "OK")
                    .data(Protocol.GetResponseDatas.RESOURCE.name(), resource.getBytes())
                    .build();
        } catch (StorageException e) {
            logger.error("Problem doing get", e);
            return Message.builder()
                    .sendType(Message.SendType.RESPONSE)
                    .messageType(Protocol.GET_RESPONSE)
                    .param(Protocol.GetResponseParams.MESSAGE.name(), e.getMessage())
                    .build();
        }
    }
}
