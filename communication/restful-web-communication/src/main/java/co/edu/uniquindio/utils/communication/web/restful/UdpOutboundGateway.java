package co.edu.uniquindio.utils.communication.web.restful;

import org.springframework.context.Lifecycle;
import org.springframework.integration.channel.RendezvousChannel;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.integration.ip.udp.MulticastSendingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class UdpOutboundGateway extends AbstractReplyProducingMessageHandler implements Lifecycle {
    private final MulticastSendingMessageHandler target;
    private final RendezvousChannel channel;

    UdpOutboundGateway(String host, int port, RendezvousChannel channel) {
        this.target = new MulticastSendingMessageHandler(host, port);
        this.setRequiresReply(true);
        this.channel = channel;
    }
    @Override
    protected Object handleRequestMessage(Message<?> requestMessage) {
        this.target.handleMessage(requestMessage);

        Message<?> response = this.channel.receive();

        //return MessageBuilder.withPayload(co.edu.uniquindio.utils.communication.message.Message.builder().sequenceNumber(1111111).build()).build();
        return response;
    }

    @Override
    public String getComponentType() {
        return "udp:outbound-gateway";
    }

    @Override
    public void start() {
        //this.setOutputChannelName(null); //TODO why
        this.setOutputChannel(null);
        this.target.start();
    }

    @Override
    public void stop() {
        this.target.stop();
    }

    @Override
    public boolean isRunning() {
        return this.target.isRunning();
    }
}
