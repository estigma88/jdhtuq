package co.edu.uniquindio.dht.it.communication.integration;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UDPTest {
    @Autowired
    private CommunicationManagerFactory communicationManagerFactory;
    @Autowired
    private RestTemplate restTemplate;

    @Before
    public void before() {
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
        Message message = Message.builder().sendType(Message.SendType.REQUEST).build();

        ResponseEntity<Message> response = restTemplate.postForEntity("http://localhost:8080/chord/messages/", message, Message.class);

        Message messageResponse = response.getBody();

        assertThat(messageResponse).isNotNull();
    }
}
