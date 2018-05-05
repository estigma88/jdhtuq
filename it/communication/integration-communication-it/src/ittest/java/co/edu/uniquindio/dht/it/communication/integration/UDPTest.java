package co.edu.uniquindio.dht.it.communication.integration;

import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageType;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UDPTest {
    @Autowired
    private CommunicationManager communicationManager;
    @Mock
    private MessageProcessor messageProcessor;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        this.communicationManager.addMessageProcessor("chord", messageProcessor);
    }

    @Test
    @Ignore
    public void sendMulticastMessage() throws IOException, InterruptedException {
        Message request = Message.builder()
                .sendType(Message.SendType.REQUEST)
                .sequenceNumber(1)
                .address(Address.builder()
                        .destination("destination")
                        .source("source")
                        .build())
                .messageType(MessageType.builder()
                        .name("testRequest")
                        .amountParams(1)
                        .build())
                .param("param1", "paramValue1")
                .build();

        Message expectedResponse = Message.builder()
                .sendType(Message.SendType.RESPONSE)
                .sequenceNumber(1)
                .address(Address.builder()
                        .destination("localhost")
                        .source("destination")
                        .build())
                .messageType(MessageType.builder()
                        .name("testResponse")
                        .amountParams(1)
                        .build())
                .param("param2", "paramValue2")
                .build();

        when(messageProcessor.process(request)).thenReturn(expectedResponse);

        communicationManager.sendMessageMultiCast(request, String.class, "param2");

        Thread.sleep(5000);

        verify(messageProcessor).process(request);

    }

    @Test
    //@Ignore
    public void sendRestfulMessage() throws IOException {
        Message request = Message.builder()
                .sendType(Message.SendType.REQUEST)
                .sequenceNumber(1)
                .address(Address.builder()
                        .destination("localhost")
                        .source("source")
                        .build())
                .messageType(MessageType.builder()
                        .name("testRequest")
                        .amountParams(1)
                        .build())
                .param("param1", "paramValue1")
                .build();

        Message expectedResponse = Message.builder()
                .sendType(Message.SendType.RESPONSE)
                .sequenceNumber(1)
                .address(Address.builder()
                        .destination("source")
                        .source("destination")
                        .build())
                .messageType(MessageType.builder()
                        .name("testResponse")
                        .amountParams(1)
                        .build())
                .param("param2", "paramValue2")
                .build();

        when(messageProcessor.process(request)).thenReturn(expectedResponse);

        String param2 = communicationManager.sendMessageUnicast(request, String.class, "param2");

        verify(messageProcessor).process(request);

        assertThat(param2).isNotNull();
        assertThat(param2).isEqualTo("paramValue2");
    }
}
