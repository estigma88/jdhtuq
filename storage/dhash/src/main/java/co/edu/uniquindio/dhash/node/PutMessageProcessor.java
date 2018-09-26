package co.edu.uniquindio.dhash.node;

import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageInputStreamProcessor;

import java.io.InputStream;

public class PutMessageProcessor implements MessageInputStreamProcessor{
    @Override
    public void process(Message request, InputStream inputStream) {
        try {
            Resource resource = serializationHandler.decode(
                    message.getData(Protocol.PutDatas.RESOURCE.name()));

            resourceManager.save(resource);

            Boolean replicate = Boolean.valueOf(message
                    .getParam(Protocol.PutParams.REPLICATE.name()));

            if (replicate) {
                dHashNode.replicateData(resource);
            }
        }catch (OverlayException | StorageException e) {
            logger.error("Error replicating data", e);
        }

    }
}
