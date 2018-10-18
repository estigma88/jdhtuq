package co.edu.uniquindio.dhash.node.processor.stream;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.FileResource;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.resource.ProgressStatus;
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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PutMessageStreamProcessorTest {
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private DHashNode dHashNode;
    @Mock
    private SerializationHandler serializationHandler;
    @Mock
    private ProgressStatus progressStatus;
    @InjectMocks
    private PutMessageStreamProcessor putMessageStreamProcessor;

    @Test
    public void put_replicate_ok() throws OverlayException, StorageException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[5]);

        Resource resource = FileResource.withInputStream()
                .id("resource")
                .size(10L)
                .inputStream(inputStream)
                .checkSum("checksum")
                .build();

        MessageStream message = MessageStream.builder()
                .message(Message.builder()
                        .id("id")
                        .messageType(Protocol.PUT)
                        .sendType(Message.SendType.RESPONSE)
                        .address(Address.builder().destination("source").source("dhash").build())
                        .param(Protocol.GetResponseParams.RESOURCE.name(), "serializableResource")
                        .param(Protocol.PutParams.REPLICATE.name(), "true")
                        .build())
                .size(10L)
                .inputStream(inputStream)
                .build();

        MessageStream expectedResponse = MessageStream.builder()
                .message(Message.builder()
                        .id("id")
                        .sendType(Message.SendType.RESPONSE)
                        .messageType(Protocol.PUT_RESPONSE)
                        .param(Protocol.PutResponseParams.TRANSFER_VALID.name(), "true")
                        .build())
                .build();

        when(serializationHandler.decode("serializableResource", inputStream)).thenReturn(resource);

        MessageStream response = putMessageStreamProcessor.process(message);

        assertThat(response).isEqualTo(expectedResponse);

        verify(resourceManager).save(eq(resource), any());
        verify(dHashNode).replicateData(eq("resource"), any());

    }

    @Test
    public void put_notReplicate_ok() throws OverlayException, StorageException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[5]);

        Resource resource = FileResource.withInputStream()
                .id("resource")
                .size(10L)
                .inputStream(inputStream)
                .checkSum("checksum")
                .build();

        MessageStream message = MessageStream.builder()
                .message(Message.builder()
                        .id("id")
                        .messageType(Protocol.PUT)
                        .sendType(Message.SendType.RESPONSE)
                        .address(Address.builder().destination("source").source("dhash").build())
                        .param(Protocol.GetResponseParams.RESOURCE.name(), "serializableResource")
                        .param(Protocol.PutParams.REPLICATE.name(), "false")
                        .build())
                .size(10L)
                .inputStream(inputStream)
                .build();

        MessageStream expectedResponse = MessageStream.builder()
                .message(Message.builder()
                        .id("id")
                        .sendType(Message.SendType.RESPONSE)
                        .messageType(Protocol.PUT_RESPONSE)
                        .param(Protocol.PutResponseParams.TRANSFER_VALID.name(), "true")
                        .build())
                .build();

        when(serializationHandler.decode("serializableResource", inputStream)).thenReturn(resource);

        MessageStream response = putMessageStreamProcessor.process(message);

        assertThat(response).isEqualTo(expectedResponse);

        verify(resourceManager).save(eq(resource), any());
        verify(dHashNode, times(0)).replicateData(any(), any());

    }


    @Test
    public void put_notReplicateException_error() throws OverlayException, StorageException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[5]);

        Resource resource = FileResource.withInputStream()
                .id("resource")
                .size(10L)
                .inputStream(inputStream)
                .checkSum("checksum")
                .build();

        MessageStream message = MessageStream.builder()
                .message(Message.builder()
                        .id("id")
                        .messageType(Protocol.PUT)
                        .sendType(Message.SendType.RESPONSE)
                        .address(Address.builder().destination("source").source("dhash").build())
                        .param(Protocol.GetResponseParams.RESOURCE.name(), "serializableResource")
                        .param(Protocol.PutParams.REPLICATE.name(), "false")
                        .build())
                .size(10L)
                .inputStream(inputStream)
                .build();

        MessageStream expectedResponse = MessageStream.builder()
                .message(Message.builder()
                        .id("id")
                        .sendType(Message.SendType.RESPONSE)
                        .messageType(Protocol.PUT_RESPONSE)
                        .param(Protocol.PutResponseParams.TRANSFER_VALID.name(), "false")
                        .param(Protocol.PutResponseParams.MESSAGE.name(), "co.edu.uniquindio.storage.StorageException: access file failed")
                        .build())
                .build();

        doThrow(new StorageException("access file failed")).when(resourceManager).save(eq(resource), any());
        when(serializationHandler.decode("serializableResource", inputStream)).thenReturn(resource);

        MessageStream response = putMessageStreamProcessor.process(message);

        assertThat(response).isEqualTo(expectedResponse);

        verify(resourceManager).save(eq(resource), any());
        verify(dHashNode, times(0)).replicateData(any(), any());

    }
}