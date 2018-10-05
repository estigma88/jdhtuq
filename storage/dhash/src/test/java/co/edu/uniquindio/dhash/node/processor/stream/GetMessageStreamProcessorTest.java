package co.edu.uniquindio.dhash.node.processor.stream;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.FileResource;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.overlay.OverlayException;
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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetMessageStreamProcessorTest {
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private DHashNode dHashNode;
    @Mock
    private SerializationHandler serializationHandler;
    @InjectMocks
    private GetMessageStreamProcessor getMessageStreamProcessor;

    @Test
    public void get_hasResource_returnTrue() throws OverlayException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[5]);

        Resource resource = FileResource.withInputStream()
                .id("resource")
                .size(10L)
                .inputStream(inputStream)
                .build();

        MessageStream expectedResponse = MessageStream.builder()
                .message(Message.builder()
                        .messageType(Protocol.GET_RESPONSE)
                        .sendType(Message.SendType.RESPONSE)
                        .address(Address.builder().destination("source").source("dhash").build())
                        .param(Protocol.GetResponseData.RESOURCE.name(), "serializableResource")
                        .build())
                .size(10L)
                .inputStream(inputStream)
                .build();

        MessageStream message = MessageStream.builder()
                .message(Message.builder()
                        .messageType(Protocol.GET)
                        .address(Address.builder().source("source").build())
                        .param(Protocol.GetParams.RESOURCE_KEY.name(), "resource")
                        .build())
                .build();

        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.find("resource")).thenReturn(resource);
        when(serializationHandler.encode(resource)).thenReturn("serializableResource");

        MessageStream response = getMessageStreamProcessor.process(message);

        assertThat(response).isEqualTo(expectedResponse);

    }


}