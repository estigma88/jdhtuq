package co.edu.uniquindio.utils.communication.integration.sender;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.dsl.IntegrationFlowDefinition;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeTypeUtils;

public class HttpSender implements MessageSender {
    private static final String MESSAGES_PATH = "messages/";
    private static final String BASE_URL = "http://{host}:8080/";
    static final String HTTP_OUTBOUND_CHANNEL = "httpOutbound";
    static final String REPLY_ORIGINAL_CHANNEL_ID_HEADER = "replyOriginChannelId";
    static final String ERROR_ORIGINAL_CHANNEL_ID_HEADER = "errorOriginChannelId";
    private final String id;
    private final Integer serverPort;
    private final IntegrationFlowContext flowContext;
    private final ApplicationContext applicationContext;
    private final MessageProcessor messageProcessor;
    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
    private StandardIntegrationFlow httpInbound;
    private StandardIntegrationFlow httpOutbound;
    private MessageSender messageSenderGateway;

    public HttpSender(String id, Integer serverPort, IntegrationFlowContext flowContext, ApplicationContext applicationContext, MessageProcessor messageProcessor, MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        this.id = id;
        this.serverPort = serverPort;
        this.flowContext = flowContext;
        this.applicationContext = applicationContext;
        this.messageProcessor = messageProcessor;
        this.mappingJackson2HttpMessageConverter = mappingJackson2HttpMessageConverter;
    }

    @Override
    public Message send(Message request) {
        if (messageSenderGateway == null) {
            throw new IllegalStateException("The HttpSender has not been started");
        }

        return messageSenderGateway.send(request);
    }

    @Override
    public void start() {
        httpInbound = getHttpInboundFlow();

        httpOutbound = getHttpOutboundFlow();

        flowContext.registration(httpInbound).id("httpInboundFlow").register();
        flowContext.registration(httpOutbound).id("httpOutboundFlow").register();

        httpInbound.start();
        httpOutbound.start();

        messageSenderGateway = (MessageSender) applicationContext.getBean("httpOutboundFlow.gateway");
    }

    @Override
    public void stop() {
        if (httpInbound == null || httpOutbound == null) {
            throw new IllegalStateException("The HttpSender has not been started");
        }

        httpInbound.stop();
        httpOutbound.stop();
    }

    @Override
    public boolean isRunning() {
        return httpInbound != null  & httpInbound.isRunning()& httpOutbound != null & httpOutbound.isRunning();
    }

    private StandardIntegrationFlow getHttpOutboundFlow() {
        return IntegrationFlows.from(MessageSender.class)
                .channel(HTTP_OUTBOUND_CHANNEL + id)
                .enrichHeaders(m -> m
                        .header(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE))
                .handle(Http.outboundGateway(BASE_URL + id + MESSAGES_PATH)
                        .httpMethod(HttpMethod.POST)
                        //The headers we created in the UDPInboundFlow are mapped here to headers in the HTTP Request
                        //So, we can retrieve those from the request origin side
                        .mappedRequestHeaders(REPLY_ORIGINAL_CHANNEL_ID_HEADER, ERROR_ORIGINAL_CHANNEL_ID_HEADER)
                        .expectedResponseType(Message.class)
                        .messageConverters(mappingJackson2HttpMessageConverter)
                        .uriVariable("host", "payload.getAddress().getDestination()")
                        .uriVariable("port", message -> serverPort))
                .get();
    }

    private StandardIntegrationFlow getHttpInboundFlow() {
        return IntegrationFlows.from(
                Http.inboundGateway(id + MESSAGES_PATH)
                        .requestMapping(m -> m.methods(HttpMethod.POST)
                                .consumes(MimeTypeUtils.APPLICATION_JSON_VALUE)
                                .produces(MimeTypeUtils.APPLICATION_JSON_VALUE))
                        //The headers we created in the HttpOutboundFlow in the HTTP Request are mapped here to headers in the Message
                        //The headers names are in lower case
                        .mappedRequestHeaders(REPLY_ORIGINAL_CHANNEL_ID_HEADER, ERROR_ORIGINAL_CHANNEL_ID_HEADER)
                        .requestPayloadType(Message.class)
                        .messageConverters(mappingJackson2HttpMessageConverter))
                .route("payload.getSendType().name()", r -> r
                        .subFlowMapping("REQUEST", s -> s
                                        .handle(messageProcessor, "process")
                        )
                        .subFlowMapping("RESPONSE", s -> s
                                //Publish the Message to two subscribers
                                .publishSubscribeChannel(p -> p
                                        //First subscriber: response to HttpInboundFlow
                                        //The Message with the default replyChannel and errorChannel are redirect using a bridge
                                        //The bridge resolves those headers and uses those channels
                                        .subscribe(IntegrationFlowDefinition::bridge)

                                        //Second subscriber: response to UDPMulticastGateway (UDPOutboundFlow)
                                        //The replyChannel and errorChannel headers are replaced with the replyOriginChannelId and errorOriginChannelId we sent at the beginning
                                        //The bridge resolves those headers and uses those channels to unblock the UDPMulticastGateway
                                        .subscribe(sub -> sub
                                                .enrichHeaders(h -> h
                                                        .headerFunction(MessageHeaders.REPLY_CHANNEL, m -> m
                                                                .getHeaders().get(REPLY_ORIGINAL_CHANNEL_ID_HEADER.toLowerCase()), true)
                                                        .headerFunction(MessageHeaders.ERROR_CHANNEL, m -> m
                                                                .getHeaders().get(ERROR_ORIGINAL_CHANNEL_ID_HEADER.toLowerCase()), true))
                                                .bridge())
                                )
                        ))

                .get();
    }
}
