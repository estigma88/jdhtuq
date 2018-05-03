package co.edu.uniquindio.dht.it.communication.integration;

import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageType;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UDPTest {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CommunicationManager communicationManager;
    @Mock
    private MessageProcessor messageProcessor;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        this.communicationManager.addMessageProcessor("chord", messageProcessor);
    }

    //@Test
    public void sendMulticastMessage() throws IOException {
        MulticastSocket multicastSocket = new MulticastSocket(2000);

        multicastSocket.joinGroup(InetAddress.getByName("224.0.0.1"));

        DatagramPacket datagramPacket;
        String string = "message";

        datagramPacket = new DatagramPacket(string.getBytes(), string.length(),
                InetAddress.getByName("224.0.0.1"), 2000);

        multicastSocket.send(datagramPacket);
    }

    @Test
    public void sendRestfulMessage() throws IOException {
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
                        .destination("source")
                        .source("destination")
                        .build())
                .messageType(MessageType.builder()
                        .name("testResponse")
                        .amountParams(1)
                        .build())
                .param("param1", "paramValue1")
                .build();

        when(messageProcessor.process(request)).thenReturn(expectedResponse);

        ResponseEntity<Message> response = restTemplate.postForEntity("http://localhost:8080/chord/messages/", request, Message.class);

        Message messageResponse = response.getBody();

        verify(messageProcessor).process(request);

        assertThat(messageResponse).isNotNull();
        assertThat(messageResponse).isEqualTo(expectedResponse);
    }
}