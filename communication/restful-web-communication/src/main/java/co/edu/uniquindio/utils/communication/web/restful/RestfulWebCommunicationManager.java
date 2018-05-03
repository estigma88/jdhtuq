package co.edu.uniquindio.utils.communication.web.restful;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageType;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import co.edu.uniquindio.utils.communication.web.restful.jackson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.ip.udp.MulticastReceivingChannelAdapter;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class RestfulWebCommunicationManager implements CommunicationManager {
    private static final String IP_MULTICAST_PROPERTY = "ip-multicast";
    private static final String PORT_MULTICAST_PROPERTY = "port-multicast";
    private final String name;
    private final RestTemplate restTemplate;
    private final String baseURL;
    private final String requestPath;
    private final int port;
    private final Observable<Message> observable;
    private final Map<String, String> parameters;
    private final IntegrationFlowContext flowContext;
    private final MessageProcessorWrapper messageProcessorWrapper;

    RestfulWebCommunicationManager(String name, RestTemplate restTemplate, String baseURL, String requestPath, int port, Observable<Message> observable, Map<String, String> parameters, IntegrationFlowContext flowContext, MessageProcessorWrapper messageProcessorWrapper) {
        this.name = name;
        this.restTemplate = restTemplate;
        this.baseURL = baseURL;
        this.requestPath = requestPath;
        this.port = port;
        this.observable = observable;
        this.parameters = parameters;
        this.flowContext = flowContext;
        this.messageProcessorWrapper = messageProcessorWrapper;
    }

    @Override
    public void init() {
        String ipMulticast = parameters.get(IP_MULTICAST_PROPERTY);
        int portMulticast = Integer.parseInt(parameters.get(PORT_MULTICAST_PROPERTY));

        StandardIntegrationFlow udpInbound = IntegrationFlows.from(new MulticastReceivingChannelAdapter(ipMulticast, portMulticast))
                .handle(messageProcessorWrapper, "process")
                .channel("udpMulticast-" + name)
                .get();

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.addMixIn(Message.class, MessageMixIn.class)
                .addMixIn(MessageType.class, MessageTypeMixIn.class)
                .addMixIn(Address.class, AddressMixIn.class)
                .addMixIn(Message.MessageBuilder.class, MessageBuilderMixIn.class)
                .addMixIn(MessageType.MessageTypeBuilder.class, MessageTypeBuilderMixIn.class)
                .addMixIn(Address.AddressBuilder.class, AddressBuilderMixIn.class);

        StandardIntegrationFlow restfulInbound = IntegrationFlows.from(
                Http.inboundGateway(name + "/" + requestPath)
                        .requestMapping(m -> m.methods(HttpMethod.POST, HttpMethod.GET)
                                .consumes("application/json")
                                .produces("application/json"))
                        .replyChannel("httpResponse-" + name))
                .channel("httpRequest-" + name)
                .transform(Transformers.fromJson(Message.class, new Jackson2JsonObjectMapper(objectMapper)))
                .handle(messageProcessorWrapper, "process")
                .transform(Transformers.toJson(new Jackson2JsonObjectMapper(objectMapper)))
                .channel("httpResponse-" + name)
                .get();

        flowContext.registration(udpInbound).register();
        flowContext.registration(restfulInbound).register();

        udpInbound.start();
        restfulInbound.start();
    }

    @Override
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn) {
        return sendMessageUnicast(message, typeReturn, null);
    }

    @Override
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn, String paramNameResult) {
        observable.notifyMessage(message);

        ResponseEntity<Message> response = restTemplate.postForEntity(baseURL + requestPath, message, Message.class, message.getAddress().getDestination(), port, name);

        Message responseMessage = response.getBody();

        observable.notifyMessage(responseMessage);

        return processResponse(responseMessage, typeReturn, paramNameResult);
    }

    @Override
    public void sendMessageUnicast(Message message) {
        sendMessageUnicast(message, Message.class);
    }

    @Override
    public <T> T sendMessageMultiCast(Message message, Class<T> typeReturn) {
        return null;
    }

    @Override
    public <T> T sendMessageMultiCast(Message message, Class<T> typeReturn, String paramNameResult) {
        return null;
    }

    @Override
    public void sendMessageMultiCast(Message message) {

    }

    @Override
    public void stopAll() {

    }

    @Override
    public void addObserver(Observer<Message> observer) {
        this.observable.addObserver(observer);
    }

    @Override
    public void removeObserver(Observer<Message> observer) {
        this.observable.removeObserver(observer);
    }

    @Override
    public void removeObserver(String name) {
        this.observable.removeObserver(name);
    }

    @Override
    public void addMessageProcessor(String name, MessageProcessor messageProcessor) {
        this.messageProcessorWrapper.updateMessageProcessor(messageProcessor);
    }

    @Override
    public void removeMessageProcessor(String name) {
        this.messageProcessorWrapper.updateMessageProcessor(null);
    }

    private <T> T processResponse(Message message, Class<T> type,
                                  String paramNameResult) {

        T typeInstance = null;

        if (message == null) {
            return null;
        }

        if (type.equals(Message.class)) {
            return (T) message;
        }

        if (type.isInterface() || type.isAnnotation() || type.isArray()) {
            throw new IllegalArgumentException("The type must a class ("
                    + type.getName() + ")");
        }

        Set<String> params = message.getParamsKey();

        String paramValue;

        if (paramNameResult == null) {

            if (params.size() != 1) {
                throw new IllegalArgumentException(
                        "The message contains more than one parameter, you can not convert to "
                                + type.getName());
            }

            String paramName = (String) params.toArray()[0];

            if (paramName == null || paramName.isEmpty()) {
                throw new IllegalArgumentException(
                        "The message contains a param name null or empty");
            }

            paramValue = message.getParam(paramName);
        } else {

            paramValue = message.getParam(paramNameResult);
        }

        if (paramValue == null || paramValue.isEmpty()) {
            return null;
        }

        try {
            Method valueOf = type
                    .getMethod("valueOf", String.class);

            typeInstance = (T) valueOf.invoke(null, paramValue);
        } catch (Exception e) {
            try {

                Constructor<T> constructorString = type
                        .getDeclaredConstructor(String.class);

                typeInstance = constructorString.newInstance(paramValue);
            } catch (Exception e1) {
                throw new IllegalArgumentException(
                        "The method valueOf(String) not must to be invoked in class "
                                + type.getName(), e1);
            }
        }

        return typeInstance;
    }
}