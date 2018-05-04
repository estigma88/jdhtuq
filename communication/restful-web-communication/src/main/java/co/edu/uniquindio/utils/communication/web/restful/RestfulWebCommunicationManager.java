package co.edu.uniquindio.utils.communication.web.restful;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.ip.dsl.Udp;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RestfulWebCommunicationManager implements CommunicationManager {
    private static final String MULTICAST_SERVER_ACTIVE_PROPERTY = "multicast-server-active";
    private static final String IP_MULTICAST_PROPERTY = "ip-multicast";
    private static final String PORT_MULTICAST_PROPERTY = "port-multicast";
    private final String name;
    private final RestTemplate restTemplate;
    private final Jackson2JsonObjectMapper jackson2JsonObjectMapper;
    private final String baseURL;
    private final String requestPath;
    private final int port;
    private final Observable<Message> observable;
    private final Map<String, String> parameters;
    private final IntegrationFlowContext flowContext;
    private final MessageProcessorWrapper messageProcessorWrapper;
    private boolean multicastServerActive;

    RestfulWebCommunicationManager(String name, RestTemplate restTemplate, Jackson2JsonObjectMapper jackson2JsonObjectMapper, String baseURL, String requestPath, int port, Observable<Message> observable, Map<String, String> parameters, IntegrationFlowContext flowContext, MessageProcessorWrapper messageProcessorWrapper) {
        this.name = name;
        this.restTemplate = restTemplate;
        this.jackson2JsonObjectMapper = jackson2JsonObjectMapper;
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
        this.multicastServerActive = Optional.ofNullable(parameters.get(MULTICAST_SERVER_ACTIVE_PROPERTY))
                .map(Boolean::valueOf)
                .orElse(false);

        if (this.multicastServerActive) {
            String ipMulticast = parameters.get(IP_MULTICAST_PROPERTY);
            int portMulticast = Integer.parseInt(parameters.get(PORT_MULTICAST_PROPERTY));

            StandardIntegrationFlow udpInbound = IntegrationFlows.from(Udp.inboundMulticastAdapter(portMulticast, ipMulticast))
                    .channel("udpMulticast-" + name)
                    .transform(Transformers.fromJson(Message.class, jackson2JsonObjectMapper))
                    .handle(messageProcessorWrapper, "process")
                    .channel((message, timeout) -> {
                        sendMessageUnicast((Message) message.getPayload());
                        return true;
                    })
                    .get();

            flowContext.registration(udpInbound).register();

            udpInbound.start();
        }

        StandardIntegrationFlow restfulInbound = IntegrationFlows.from(
                Http.inboundGateway(name + requestPath)
                        .requestMapping(m -> m.methods(HttpMethod.POST, HttpMethod.GET)
                                .consumes("application/json")
                                .produces("application/json"))
                        .replyChannel("httpResponse-" + name))
                .channel("httpRequest-" + name)
                .transform(Transformers.fromJson(Message.class, jackson2JsonObjectMapper))
                .handle(messageProcessorWrapper, "process")
                .transform(Transformers.toJson(jackson2JsonObjectMapper))
                .channel("httpResponse-" + name)
                .get();

        flowContext.registration(restfulInbound).register();

        restfulInbound.start();
    }

    @Override
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn) {
        return sendMessageUnicast(message, typeReturn, null);
    }

    @Override
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn, String paramNameResult) {
        observable.notifyMessage(message);

        ResponseEntity<Message> response = restTemplate.postForEntity(baseURL + name + requestPath, message, Message.class, message.getAddress().getDestination(), port);

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
        return sendMessageMultiCast(message, typeReturn, null);
    }

    @Override
    public <T> T sendMessageMultiCast(Message message, Class<T> typeReturn, String paramNameResult) {
        if (multicastServerActive) {
            String ipMulticast = parameters.get(IP_MULTICAST_PROPERTY);
            int portMulticast = Integer.parseInt(parameters.get(PORT_MULTICAST_PROPERTY));

            try {
                MulticastSocket multicastSocket = new MulticastSocket(portMulticast);

                multicastSocket.joinGroup(InetAddress.getByName(ipMulticast));

                String stringMessage = jackson2JsonObjectMapper.getObjectMapper().writeValueAsString(message);

                DatagramPacket datagramPacket = new DatagramPacket(stringMessage.getBytes(), stringMessage.length(),
                        InetAddress.getByName(ipMulticast), portMulticast);

                multicastSocket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        } else {
            throw new IllegalStateException("Multicast server is not active or you must call 'init'");
        }
    }

    @Override
    public void sendMessageMultiCast(Message message) {
        sendMessageMultiCast(message, Message.class);
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
