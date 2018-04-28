package co.edu.uniquindio.utils.communication.web.restful;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RestfulWebCommunicationManagerFactory implements CommunicationManagerFactory {
    private final Map<String, RestfulWebCommunicationManager> communicationManagerMap;
    private final RestTemplate restTemplate;
    private final String baseURL;
    private final String requestPath;
    private final int port;
    private final Observable<Message> observable;
    private final Map<String, Map<String, String>> paramsByCommunication;
    private final IntegrationFlowContext flowContext;

    public RestfulWebCommunicationManagerFactory(RestTemplate restTemplate, String baseURL, String requestPath, int port, Observable<Message> observable, Map<String, Map<String, String>> paramsByCommunication, IntegrationFlowContext flowContext) {
        this.restTemplate = restTemplate;
        this.baseURL = baseURL;
        this.requestPath = requestPath;
        this.port = port;
        this.observable = observable;
        this.paramsByCommunication = paramsByCommunication;
        this.flowContext = flowContext;

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
        RestfulWebCommunicationManager restfulWebCommunicationClient = new RestfulWebCommunicationManager(name, restTemplate, baseURL, requestPath, port, observable, paramsByCommunication.get(name), flowContext);

        restfulWebCommunicationClient.init();

        communicationManagerMap.put(name, restfulWebCommunicationClient);

        return restfulWebCommunicationClient;
    }

    MessageProcessor getMessageProcessor(String communicationName) {
        return Optional.ofNullable(communicationManagerMap.get(communicationName))
                .map(RestfulWebCommunicationManager::getMessageProcessor)
                .orElse(r -> Message.builder().build());
    }
}
