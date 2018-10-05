package co.edu.uniquindio.dhash.node.processor;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContainProcessorTest {
    @Mock
    private DHashNode dHashNode;
    @Mock
    private ResourceManager resourceManager;
    @InjectMocks
    private ContainProcessor containProcessor;

    @Test
    public void contain_hasResource_returnTrue() throws OverlayException {
        Message message = Message.builder()
                .messageType(Protocol.CONTAIN)
                .address(Address.builder()
                        .source("source")
                        .build())
                .param(Protocol.ContainParams.RESOURCE_KEY.name(), "resource")
                .build();

        Message expectedResponse = Message.builder()
                .sendType(Message.SendType.RESPONSE)
                .messageType(Protocol.CONTAIN_RESPONSE)
                .address(Address.builder()
                        .source("dhash")
                        .destination("source")
                        .build())
                .param(Protocol.ContainResponseParams.HAS_RESOURCE.name(), "true")
                .build();

        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(true);

        Message response = containProcessor.process(message);

        assertThat(response).isEqualTo(expectedResponse);

    }

    @Test
    public void contain_notHasResource_returnFalse() throws OverlayException {
        Message message = Message.builder()
                .messageType(Protocol.CONTAIN)
                .address(Address.builder()
                        .source("source")
                        .build())
                .param(Protocol.ContainParams.RESOURCE_KEY.name(), "resource")
                .build();

        Message expectedResponse = Message.builder()
                .sendType(Message.SendType.RESPONSE)
                .messageType(Protocol.CONTAIN_RESPONSE)
                .address(Address.builder()
                        .source("dhash")
                        .destination("source")
                        .build())
                .param(Protocol.ContainResponseParams.HAS_RESOURCE.name(), "false")
                .build();

        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(false);

        Message response = containProcessor.process(message);

        assertThat(response).isEqualTo(expectedResponse);

    }


}