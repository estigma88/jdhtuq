package co.edu.uniquindio.dhash.node;

import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageStreamProcessor;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;

public class PutMessageInputStreamProcessor implements MessageStreamProcessor {
    private static final Logger logger = Logger
            .getLogger(PutMessageInputStreamProcessor.class);

    private final ResourceManager resourceManager;
    private final DHashNode dHashNode;
    private final SerializationHandler serializationHandler;

    public PutMessageInputStreamProcessor(ResourceManager resourceManager, DHashNode dHashNode, SerializationHandler serializationHandler) {
        this.resourceManager = resourceManager;
        this.dHashNode = dHashNode;
        this.serializationHandler = serializationHandler;
    }

    @Override
    public void process(Message message, InputStream inputStream, OutputStream outputStrea) {
        try {
            Resource resource = serializationHandler.decode(
                    message.getParam(Protocol.PutDatas.RESOURCE.name()), inputStream);

            resourceManager.save(resource);

            Boolean replicate = Boolean.valueOf(message
                    .getParam(Protocol.PutParams.REPLICATE.name()));

            if (replicate) {
                dHashNode.replicateData(resource.getId(), (name, current, size) -> {});
            }
        }catch (OverlayException | StorageException e) {
            logger.error("Error replicating data", e);
        }

    }
}
