package co.edu.uniquindio.dhash.node.processor.stream;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.FileResource;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PutMessageStreamProcessorTest {
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private DHashNode dHashNode;
    @Mock
    private SerializationHandler serializationHandler;
    @InjectMocks
    private PutMessageStreamProcessor putMessageStreamProcessor;

    @Test
    public void put_replicate_ok() throws OverlayException, StorageException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[5]);

        Resource resource = FileResource.withInputStream()
                .id("resource")
                .size(10L)
                .inputStream(inputStream)
                .build();

        MessageStream message = MessageStream.builder()
                .message(Message.builder()
                        .messageType(Protocol.PUT)
                        .sendType(Message.SendType.RESPONSE)
                        .address(Address.builder().destination("source").source("dhash").build())
                        .param(Protocol.GetResponseData.RESOURCE.name(), "serializableResource")
                        .param(Protocol.PutParams.REPLICATE.name(), "true")
                        .build())
                .size(10L)
                .inputStream(inputStream)
                .build();

        when(dHashNode.getName()).thenReturn("dhash");
        when(serializationHandler.decode("serializableResource", inputStream)).thenReturn(resource);

        MessageStream response = putMessageStreamProcessor.process(message);

        assertThat(response).isNull();

        verify(resourceManager).save(resource);
        verify(dHashNode).replicateData(eq("resource"), any());

    }
}