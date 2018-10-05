package co.edu.uniquindio.dhash.node.processor;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
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
public class MessageProcessorGatewayTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private DHashNode dHashNode;
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private MessageProcessor messageProcessor;
    @InjectMocks
    private MessageProcessorGateway messageProcessorGateway;

    @Test
    public void new_construct() {
        assertThat(messageProcessorGateway.getMessageProcessorMap().size()).isEqualTo(1);
        assertThat(messageProcessorGateway.getMessageProcessorMap().get(Protocol.CONTAIN)).isNotNull();
    }

    @Test
    public void process_rightMessageType_returnResponse() {
        messageProcessorGateway.getMessageProcessorMap().put(Protocol.CONTAIN, messageProcessor);

        Message expectedResponse = Message.builder().build();

        Message message = Message.builder()
                .messageType(Protocol.CONTAIN)
                .build();

        when(messageProcessor.process(message)).thenReturn(expectedResponse);

        Message response = messageProcessorGateway.process(message);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    public void process_wrongMessageType_throwException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Message " + Protocol.GET + " was not found");

        Message message = Message.builder()
                .messageType(Protocol.GET)
                .build();

        messageProcessorGateway.process(message);
    }
}