package co.edu.uniquindio.dhash.node.processor.stream;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.LogProgressStatus;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.resource.ProgressStatus;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;
import co.edu.uniquindio.utils.communication.transfer.MessageStreamProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PutMessageStreamProcessor implements MessageStreamProcessor {
    private final ResourceManager resourceManager;
    private final DHashNode dHashNode;
    private final SerializationHandler serializationHandler;

    PutMessageStreamProcessor(ResourceManager resourceManager, DHashNode dHashNode, SerializationHandler serializationHandler) {
        this.resourceManager = resourceManager;
        this.dHashNode = dHashNode;
        this.serializationHandler = serializationHandler;
    }

    @Override
    public MessageStream process(MessageStream messageStream) {
        try {
            Message message = messageStream.getMessage();

            Resource resource = serializationHandler.decode(
                    message.getParam(Protocol.PutDatas.RESOURCE.name()), messageStream.getInputStream());

            resourceManager.save(resource);

            Boolean replicate = Boolean.valueOf(message
                    .getParam(Protocol.PutParams.REPLICATE.name()));

            if (replicate) {
                ProgressStatus progressStatus = LogProgressStatus.builder()
                        .id(resource.getId())
                        .build();

                dHashNode.replicateData(resource.getId(), progressStatus);
            }

            return null;
        } catch (OverlayException | StorageException e) {
            log.error("Error putting data", e);
            throw new IllegalStateException("Error putting data", e);
        }

    }
}
