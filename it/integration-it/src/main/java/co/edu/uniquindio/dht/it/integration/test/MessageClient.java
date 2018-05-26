package co.edu.uniquindio.dht.it.integration.test;

import co.edu.uniquindio.utils.communication.message.Message;
import org.springframework.web.client.RestTemplate;

public class MessageClient {
    private static final String MESSAGES_PATH = "jdhtuq/messages/";
    private static final String BASE_URL = "http://{host}:{port}/";
    private final Integer port;
    private final RestTemplate restTemplate;

    public MessageClient(Integer port, RestTemplate restTemplate) {
        this.port = port;
        this.restTemplate = restTemplate;
    }

    public Message send(Message request) {
        return restTemplate.postForEntity(BASE_URL + MESSAGES_PATH, request, Message.class, request.getAddress().getDestination(), port).getBody();
    }
}
