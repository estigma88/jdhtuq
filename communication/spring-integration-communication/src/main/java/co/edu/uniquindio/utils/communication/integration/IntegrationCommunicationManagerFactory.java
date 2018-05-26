package co.edu.uniquindio.utils.communication.integration;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.integration.sender.ExtendedMessageTransformer;
import co.edu.uniquindio.utils.communication.integration.sender.HttpSender;
import co.edu.uniquindio.utils.communication.integration.sender.MessageSender;
import co.edu.uniquindio.utils.communication.integration.sender.UDPMulticastSender;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;

import java.util.Map;
import java.util.Optional;

public class IntegrationCommunicationManagerFactory implements CommunicationManagerFactory {
    private final Jackson2JsonObjectMapper jackson2JsonObjectMapper;
    private final int port;
    private final Observable<Message> observable;
    private final Map<String, Map<String, String>> paramsByCommunication;
    private final IntegrationFlowContext flowContext;
    private final MessageProcessorWrapper messageProcessorWrapper;
    private final ApplicationContext applicationContext;
    private final MessageResponseProcessor messageResponseProcessor;
    private final ExtendedMessageTransformer extendedMessageTransformer;

    public IntegrationCommunicationManagerFactory(Jackson2JsonObjectMapper jackson2JsonObjectMapper, int port, Observable<Message> observable, Map<String, Map<String, String>> paramsByCommunication, IntegrationFlowContext flowContext, MessageProcessorWrapper messageProcessorWrapper, ApplicationContext applicationContext, MessageResponseProcessor messageResponseProcessor, ExtendedMessageTransformer extendedMessageTransformer) {
        this.jackson2JsonObjectMapper = jackson2JsonObjectMapper;
        this.port = port;
        this.observable = observable;
        this.paramsByCommunication = paramsByCommunication;
        this.flowContext = flowContext;
        this.messageProcessorWrapper = messageProcessorWrapper;
        this.applicationContext = applicationContext;
        this.messageResponseProcessor = messageResponseProcessor;
        this.extendedMessageTransformer = extendedMessageTransformer;
    }


    @Override
    public CommunicationManager newCommunicationManager(String name) {
        boolean multicastActive = Optional.ofNullable(paramsByCommunication.get(name))
                .map(p -> p.get("multicast-server-active"))
                .map(Boolean::valueOf)
                .orElse(false);

        IntegrationCommunicationManager restfulWebCommunicationClient = new IntegrationCommunicationManager(observable, messageProcessorWrapper, getDirectSender(name), getMulticastSender(name), messageResponseProcessor, multicastActive);

        restfulWebCommunicationClient.init();

        return restfulWebCommunicationClient;
    }

    private MessageSender getMulticastSender(String name) {
        String group = Optional.ofNullable(paramsByCommunication.get(name))
                .map(p -> p.get("ip-multicast"))
                .orElse("");
        Integer groupPort = Optional.ofNullable(paramsByCommunication.get(name))
                .map(p -> p.get("port-multicast"))
                .map(Integer::parseInt)
                .orElse(0);

        return new UDPMulticastSender(name, flowContext, applicationContext, messageProcessorWrapper, extendedMessageTransformer, jackson2JsonObjectMapper, group, groupPort);
    }

    private MessageSender getDirectSender(String name) {
        return new HttpSender(name, port, flowContext, applicationContext, messageProcessorWrapper, new MappingJackson2HttpMessageConverter(jackson2JsonObjectMapper.getObjectMapper()));
    }
}
