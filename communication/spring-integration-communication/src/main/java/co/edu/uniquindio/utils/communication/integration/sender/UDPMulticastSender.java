package co.edu.uniquindio.utils.communication.integration.sender;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.dsl.HeaderEnricherSpec;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.ip.dsl.Udp;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeTypeUtils;

import static co.edu.uniquindio.utils.communication.integration.sender.HttpSender.*;

public class UDPMulticastSender implements MessageSender {
    private final String id;
    private final IntegrationFlowContext flowContext;
    private final ApplicationContext applicationContext;
    private final MessageProcessor messageProcessor;
    private final ExtendedMessageTransformer extendedMessageTransformer;
    private final Jackson2JsonObjectMapper jackson2JsonObjectMapper;
    private final String group;
    private final Integer port;
    private StandardIntegrationFlow udpInbound;
    private StandardIntegrationFlow udpOutbound;
    private MessageSender messageSenderGateway;

    public UDPMulticastSender(String id, IntegrationFlowContext flowContext, ApplicationContext applicationContext, MessageProcessor messageProcessor, ExtendedMessageTransformer extendedMessageTransformer, Jackson2JsonObjectMapper jackson2JsonObjectMapper, String group, Integer port) {
        this.id = id;
        this.flowContext = flowContext;
        this.applicationContext = applicationContext;
        this.messageProcessor = messageProcessor;
        this.extendedMessageTransformer = extendedMessageTransformer;
        this.jackson2JsonObjectMapper = jackson2JsonObjectMapper;
        this.group = group;
        this.port = port;
    }

    @Override
    public Message send(Message request) {

        if (messageSenderGateway == null) {
            throw new IllegalStateException("The UDPMulticastSender has not been started");
        }

        return messageSenderGateway.send(request);
    }

    @Override
    public void start() {
        udpInbound = getUDPInboundFlow();

        udpOutbound = getUDPOutboundFlow();

        flowContext.registration(udpInbound).id("udpInboundFlow-" + id).register();
        flowContext.registration(udpOutbound).id("udpOutboundFlow-" + id).register();

        udpInbound.start();
        udpOutbound.start();

        messageSenderGateway = (MessageSender) applicationContext.getBean("udpOutboundFlow-" + id + ".gateway");
    }

    @Override
    public void stop() {
        if (udpInbound == null || udpOutbound == null) {
            throw new IllegalStateException("The UDPMulticastSender has not been started");
        }

        udpInbound.stop();
        udpOutbound.stop();
    }

    @Override
    public boolean isRunning() {
        return udpInbound != null & udpInbound.isRunning() & udpOutbound != null & udpOutbound.isRunning();
    }

    private StandardIntegrationFlow getUDPOutboundFlow() {
        //UDPMulticastGateway is the gateway through we are going to consume this flow
        return IntegrationFlows.from(MessageSender.class)
                .enrichHeaders(m -> m
                        .header(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE))
                //Persist the replayChannel and  errorChannel headers, getting IDs
                .enrichHeaders(HeaderEnricherSpec::headerChannelsToString)
                //Transform the message adding the replayChannel and errorChannel IDs as a part of the payload
                //To retrieve later after the response arrives
                .transform(extendedMessageTransformer)
                .transform(Transformers.toJson(jackson2JsonObjectMapper))
                .handle(Udp.outboundMulticastAdapter(group, port))
                .get();
    }


    private StandardIntegrationFlow getUDPInboundFlow() {
        return IntegrationFlows.from(Udp.inboundMulticastAdapter(port, group))
                .enrichHeaders(m -> m
                        .header(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE))
                .transform(Transformers.fromJson(ExtendedMessage.class, jackson2JsonObjectMapper))
                //Add new headers to the Message named replyOriginChannelId and errorOriginChannelId from the payload we receive
                .enrichHeaders(h -> h
                        .headerFunction(REPLY_ORIGINAL_CHANNEL_ID_HEADER, m -> ((ExtendedMessage) m.getPayload()).getReplyOriginChannelId())
                        .headerFunction(ERROR_ORIGINAL_CHANNEL_ID_HEADER, m -> ((ExtendedMessage) m.getPayload()).getErrorOriginChannelId()))
                //Get the read data we want to handle
                .transform(ExtendedMessage::getData)
                .handle(messageProcessor, "process")
                //Publish a Message response using the httpOutbound channel
                .publishSubscribeChannel(p -> p
                        .subscribe(s -> s
                                //The inboundMulticastAdapter does not have replyChannel and errorChannel by default
                                //So, we add a NullChannel where httpOutbound is going to respond
                                //We do not care about if this request is successful, this communication flow is still unreliable
                                .enrichHeaders(h -> h
                                        .header(MessageHeaders.REPLY_CHANNEL, new NullChannel(), true)
                                        .header(MessageHeaders.ERROR_CHANNEL, new NullChannel(), true))
                                //Send the message to httpOutbound channel
                                .channel(HTTP_OUTBOUND_CHANNEL + id)))
                .get();
    }
}
