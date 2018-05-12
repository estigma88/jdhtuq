package co.edu.uniquindio.utils.communication.web.restful;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.channel.RendezvousChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.HeaderEnricherSpec;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.ip.dsl.Udp;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
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
    private final ApplicationContext applicationContext;
    private boolean multicastServerActive;
    private MessagingTemplate messagingTemplateUdp;
    private Service udpService;
    private Service httpService;

    RestfulWebCommunicationManager(String name, RestTemplate restTemplate, Jackson2JsonObjectMapper jackson2JsonObjectMapper, String baseURL, String requestPath, int port, Observable<Message> observable, Map<String, String> parameters, IntegrationFlowContext flowContext, MessageProcessorWrapper messageProcessorWrapper, ApplicationContext applicationContext) {
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
        this.applicationContext = applicationContext;
    }

    @Override
    public void init() {

        RendezvousChannel channel = MessageChannels.rendezvous("messageid").get();

        StandardIntegrationFlow restfulInbound = IntegrationFlows.from(
                Http.inboundGateway(name + requestPath)
                        .requestMapping(m -> m.methods(HttpMethod.POST, HttpMethod.GET)
                                .consumes("application/json")
                                .produces("application/json"))
                        .mappedRequestHeaders("replyChannelId", "errorChannelId")
                        .requestPayloadType(Message.class)
                        .messageConverters(new MappingJackson2HttpMessageConverter(jackson2JsonObjectMapper.getObjectMapper()))
                //.replyChannel("httpResponse-" + name)
        )
                //.channel("httpRequest-" + name)
                //.transform(Transformers.fromJson(Message.class, jackson2JsonObjectMapper))
                .log("3333333")
                /*.filter(new GenericSelector<Message>() {
                    @Override
                    public boolean accept(Message source) {
                        return source.getSendType().equals(Message.SendType.RESPONSE);
                    }
                })*/
                .route("payload.getSendType().name()", r -> r
                        .subFlowMapping("REQUEST", s -> s
                                        .handle(messageProcessorWrapper, "process")
                                //.log("4444444")
                                //.transform(Transformers.toJson(jackson2JsonObjectMapper))
                                //.channel("httpResponse-" + name)
                        )
                        .subFlowMapping("RESPONSE", s -> s
                                .publishSubscribeChannel(p -> p.subscribe(sub -> sub
                                        .log("sbridge2")
                                        .bridge())
                                )
                                .publishSubscribeChannel(p -> p.subscribe(sub -> sub
                                        .enrichHeaders(h -> h.headerExpression("replyChannel", "headers.get('replychannelid')", true)
                                                .headerExpression("errorChannel", "headers.get('errorchannelid')", true))
                                        .log("sbridge1")
                                        .bridge())
                                )
                        ))
                //.channel(channel)//TODO Dynamic channel????
                //.handle(messageProcessorWrapper, "process")
                //.log("4444444")
                //.transform(Transformers.toJson(jackson2JsonObjectMapper))
                //.log("5555555")
                //.channel("httpResponse-" + name)
                .get();

        Map<String, Object> headersMapHttp = new HashMap<>();
        headersMapHttp.put("receiveTimeout", 5000);
        headersMapHttp.put(MessageHeaders.CONTENT_TYPE, "application/json");
        //headersMapHttp.put(MessageHeaders.REPLY_CHANNEL, null);

        StandardIntegrationFlow restfulOutbound = IntegrationFlows.from(Service.class)
                .channel("xxxxxxxx")
                .log("222222")
                .enrichHeaders(headersMapHttp)
                .handle(Http.outboundGateway(baseURL + name + requestPath)
                        .httpMethod(HttpMethod.POST)
                        .mappedRequestHeaders("replyChannelId", "errorChannelId")
                        .expectedResponseType(Message.class)
                        .messageConverters(new MappingJackson2HttpMessageConverter(jackson2JsonObjectMapper.getObjectMapper()))
                        .uriVariable("host", "payload.getAddress().getDestination()")
                        .uriVariable("port", message -> port))
                .get();

        flowContext.registration(restfulInbound).id("httpInboundFlow-" + name).register();
        flowContext.registration(restfulOutbound).id("httpOutboundFlow-" + name).register();

        restfulInbound.start();
        restfulOutbound.start();

        this.multicastServerActive = Optional.ofNullable(parameters.get(MULTICAST_SERVER_ACTIVE_PROPERTY))
                .map(Boolean::valueOf)
                .orElse(false);


        if (this.multicastServerActive) {
            String ipMulticast = parameters.get(IP_MULTICAST_PROPERTY);
            int portMulticast = Integer.parseInt(parameters.get(PORT_MULTICAST_PROPERTY));

            Map<String, Object> headersMapMultIn = new HashMap<>();
            headersMapMultIn.put("receiveTimeout", 5000);
            headersMapMultIn.put(MessageHeaders.CONTENT_TYPE, "application/json");
            //headersMapMultIn.put(MessageHeaders.REPLY_CHANNEL, "xxxxxxxx");

            MessageChannel messageChannel = new DirectChannel();
            StandardIntegrationFlow udpInbound = IntegrationFlows.from(
                    //new MulticastReceivingChannelAdapter(ipMulticast, portMulticast)
                    Udp.inboundMulticastAdapter(portMulticast, ipMulticast)
                            //.outputChannel((MessageChannel) applicationContext.getBean("xxxxxxxx"))
                            .outputChannel(messageChannel)
            )
                    //.channel(messageChannel)
                    .enrichHeaders(headersMapMultIn)
                    .transform(Transformers.fromJson(ExtendedMessage.class, jackson2JsonObjectMapper))
                    .log("111111")
                    .enrichHeaders(h -> h.headerExpression("replyChannelId", "payload.replyChannelId")
                            .headerExpression("errorChannelId", "payload.errorChannelId"))
                    .log("111111")
                    .transform(ExtendedMessage::getData)
                    .log("111111")
                    .handle(messageProcessorWrapper, "process")
                    .log("gggggg")
                    .publishSubscribeChannel(p -> p.subscribe(s -> s.log("nnnnnnnnnn").enrichHeaders(h -> h.header("replyChannel", new NullChannel(), true)
                            .header("errorChannel", new NullChannel(), true)).channel("xxxxxxxx")))
                    //.channel("xxxxxxxx")
                    //.log("pppppp")
                    //.channel(messageChannel)
                    //.channel("xxxxxxxx")
                    //.transform(Transformers.toJson(jackson2JsonObjectMapper))
                    .get();

            Map<String, Object> headersMapMultOut = new HashMap<>();
            headersMapMultOut.put("receiveTimeout", 5000);
            headersMapMultOut.put(MessageHeaders.CONTENT_TYPE, "application/json");
            //headersMapMultOut.put(MessageHeaders.REPLY_CHANNEL, null);

            StandardIntegrationFlow udpOutbound = IntegrationFlows.from(Service.class)
                    .log("0000000")
                    .enrichHeaders(headersMapMultOut)
                    .enrichHeaders(HeaderEnricherSpec::headerChannelsToString)
                    .transform(new Trans())
                    .transform(Transformers.toJson(jackson2JsonObjectMapper))
                    .handle(Udp.outboundMulticastAdapter(ipMulticast, portMulticast))
                    //.handle(new MulticastSendingMessageHandler(ipMulticast, portMulticast))
                    //.handle(new UdpOutboundGateway(ipMulticast, portMulticast, channel))
                    //.log("zzzzzz")
                    .get();


            flowContext.registration(udpInbound).id("udpInboundFlow-" + name).register();
            flowContext.registration(udpOutbound).id("udpOutboundFlow-" + name).register();

            udpInbound.start();
            udpOutbound.start();

            messagingTemplateUdp = flowContext.messagingTemplateFor("udpOutboundFlow-" + name);

        }

        udpService = (Service) applicationContext.getBean("udpOutboundFlow-chord.gateway");
        httpService = (Service) applicationContext.getBean("httpOutboundFlow-chord.gateway");
    }

    @Override
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn) {
        return sendMessageUnicast(message, typeReturn, null);
    }

    @Override
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn, String paramNameResult) {
        observable.notifyMessage(message);

        /*MessagingTemplate messagingTemplate = flowContext.messagingTemplateFor("httpOutboundFlow-" + name);

        Map<String, Object> headersMap = new HashMap<>();
        headersMap.put(MessageHeaders.CONTENT_TYPE, "application/json");

        MessageHeaders headers = new MessageHeaders(headersMap);

        org.springframework.messaging.Message<Message> requestMessage = MessageBuilder.createMessage(message, headers);
        org.springframework.messaging.Message<?> response = messagingTemplate.sendAndReceive(requestMessage);

        Message responseMessage = (Message) response.getPayload();*/

        //could be null
        /*ResponseEntity<Message> response = restTemplate.postForEntity(baseURL + name + requestPath, message, Message.class, message.getAddress().getDestination(), port);

        Message responseMessage = response.getBody();

        observable.notifyMessage(responseMessage);*/

        Message responseMessage = httpService.send(message);

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
            org.springframework.messaging.Message<Message> requestMessage = MessageBuilder.withPayload(message)
                    .setHeader(MessageHeaders.CONTENT_TYPE, "application/json")
                    //.setHeader(MessageHeaders.REPLY_CHANNEL, "httpRequest-" + name)
                    //.setHeader("receiveTimeout", 5000)
                    .build();
            /*org.springframework.messaging.Message<?> response = messagingTemplateUdp.sendAndReceive(requestMessage);

            Message responseMessage = (Message) response.getPayload();*/

            //messagingTemplateUdp.send(requestMessage);

            Message responseMessage = udpService.send(message);


            return processResponse(responseMessage, typeReturn, paramNameResult);
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

    /*@Bean
    public IntegrationFlow routeFlow() {
        return f -> f.<Integer, Boolean>route(p -> p % 2 == 0, m -> m.channelMapping("true", "evenChannel").subFlowMapping("false", sf -> sf.<Integer>handle((p, h) -> p * 3))).transform(Object::toString).channel(c -> c.queue("oddChannel"));
    }*/

}
