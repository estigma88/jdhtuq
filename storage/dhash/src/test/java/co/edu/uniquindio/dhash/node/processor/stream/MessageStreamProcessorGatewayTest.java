package co.edu.uniquindio.dhash.node.processor.stream;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;
import co.edu.uniquindio.utils.communication.transfer.MessageStreamProcessor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageStreamProcessorGatewayTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private DHashNode dHashNode;
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private MessageStreamProcessor messageStreamProcessor;
    @InjectMocks
    private MessageStreamProcessorGateway messageStreamProcessorGateway;

    @Test
    public void new_construct() {
        assertThat(messageStreamProcessorGateway.getMessageStreamProcessorMap().size()).isEqualTo(2);
        assertThat(messageStreamProcessorGateway.getMessageStreamProcessorMap().get(Protocol.GET)).isNotNull();
        assertThat(messageStreamProcessorGateway.getMessageStreamProcessorMap().get(Protocol.PUT)).isNotNull();
    }

    @Test
    public void process_rightMessageType_returnResponse() {
        messageStreamProcessorGateway.getMessageStreamProcessorMap().put(Protocol.GET, messageStreamProcessor);

        MessageStream expectedResponse = MessageStream.builder()
                .message(Message.builder()
                        .build())
                .build();

        MessageStream message = MessageStream.builder()
                .message(Message.builder()
                        .messageType(Protocol.GET)
                        .build())
                .build();

        when(messageStreamProcessor.process(message)).thenReturn(expectedResponse);

        MessageStream response = messageStreamProcessorGateway.process(message);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    public void process_wrongMessageType_throwException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Message " + Protocol.CONTAIN + " was not found");

        MessageStream message = MessageStream.builder()
                .message(Message.builder()
                        .messageType(Protocol.CONTAIN)
                        .build())
                .build();

        messageStreamProcessorGateway.process(message);
    }
}