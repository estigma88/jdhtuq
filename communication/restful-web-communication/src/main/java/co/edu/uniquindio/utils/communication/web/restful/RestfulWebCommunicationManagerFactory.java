package co.edu.uniquindio.utils.communication.web.restful;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class RestfulWebCommunicationManagerFactory implements CommunicationManagerFactory {
    private final Map<String, RestfulWebCommunicationManager> communicationManagerMap;
    private final RestTemplate restTemplate;
    private final Jackson2JsonObjectMapper jackson2JsonObjectMapper;
    private final String baseURL;
    private final String requestPath;
    private final int port;
    private final Observable<Message> observable;
    private final Map<String, Map<String, String>> paramsByCommunication;
    private final IntegrationFlowContext flowContext;
    private final MessageProcessorWrapper messageProcessorWrapper;

    public RestfulWebCommunicationManagerFactory(RestTemplate restTemplate, Jackson2JsonObjectMapper jackson2JsonObjectMapper, String baseURL, String requestPath, int port, Observable<Message> observable, Map<String, Map<String, String>> paramsByCommunication, IntegrationFlowContext flowContext, MessageProcessorWrapper messageProcessorWrapper) {
        this.restTemplate = restTemplate;
        this.jackson2JsonObjectMapper = jackson2JsonObjectMapper;
        this.baseURL = baseURL;
        this.requestPath = requestPath;
        this.port = port;
        this.observable = observable;
        this.paramsByCommunication = paramsByCommunication;
        this.flowContext = flowContext;
        this.messageProcessorWrapper = messageProcessorWrapper;

        communicationManagerMap = new HashMap<>();
    }


    @Override
    public CommunicationManager newCommunicationManager(String name) {
        RestfulWebCommunicationManager restfulWebCommunicationClient = new RestfulWebCommunicationManager(name, restTemplate, jackson2JsonObjectMapper, baseURL, requestPath, port, observable, paramsByCommunication.get(name), flowContext, messageProcessorWrapper);

        restfulWebCommunicationClient.init();

        communicationManagerMap.put(name, restfulWebCommunicationClient);

        return restfulWebCommunicationClient;
    }
}
