package co.edu.uniquindio.utils.communication.web.restful;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RestfulWebCommunicationManagerFactory implements CommunicationManagerFactory {
    private final Map<String, RestfulWebCommunicationClient> communicationManagerMap;
    private final RestTemplate restTemplate;
    private final String baseURL;
    private final String requestPath;
    private final int port;
    private final Observable<Message> observable;

    public RestfulWebCommunicationManagerFactory(RestTemplate restTemplate, String baseURL, String requestPath, int port, Observable<Message> observable) {
        this.restTemplate = restTemplate;
        this.baseURL = baseURL;
        this.requestPath = requestPath;
        this.port = port;
        this.observable = observable;

        /*ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.addMixIn(Message.class, MessageMixIn.class)
                .addMixIn(MessageType.class, MessageTypeMixIn.class)
                .addMixIn(Address.class, AddressMixIn.class)
                .addMixIn(Message.MessageBuilder.class, MessageBuilderMixIn.class)
                .addMixIn(MessageType.MessageTypeBuilder.class, MessageTypeBuilderMixIn.class)
                .addMixIn(Address.AddressBuilder.class, AddressBuilderMixIn.class);*/

        communicationManagerMap = new HashMap<>();
    }


    @Override
    public CommunicationManager newCommunicationManager(String name) {
        RestfulWebCommunicationClient restfulWebCommunicationClient = new RestfulWebCommunicationClient(name, restTemplate, baseURL, requestPath, port, observable);

        communicationManagerMap.put(name, restfulWebCommunicationClient);

        return restfulWebCommunicationClient;
    }

    MessageProcessor getMessageProcessor(String communicationName) {
        return Optional.ofNullable(communicationManagerMap.get(communicationName))
                .map(RestfulWebCommunicationClient::getMessageProcessor)
                .orElse(r -> Message.builder().build());
    }
}
